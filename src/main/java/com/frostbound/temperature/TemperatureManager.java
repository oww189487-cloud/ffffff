package com.frostbound.temperature;

import com.frostbound.Config;
import com.frostbound.network.TempSyncPayload;
import com.frostbound.registry.ModAttachments;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Температура тела игрока.
 * Раз в секунду: считаем «эффективную» температуру воздуха (климат + тепло + одежда),
 * двигаем температуру тела, накладываем эффекты и отправляем данные клиенту.
 */
public final class TemperatureManager {
    public static final float NORMAL = 36.6F;
    public static final float MIN = 28.0F;

    /** Пороги: слабость -> замедление -> обморожение (урон). */
    public static final float CHILLED = 35.0F;
    public static final float FREEZING = 33.0F;
    public static final float FROSTBITE = 31.0F;

    public record Snapshot(float body, float air, float heat, float insulation,
                           double worldDrop, float storm) {
        public float effective() {
            return air + insulation;
        }
    }

    public static Snapshot sample(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        float heat = HeatSources.heatAt(level, pos);
        float air = Climate.air(level, pos) + heat;
        float insulation = Insulation.total(player);
        float body = player.getData(ModAttachments.BODY_TEMP.get());
        return new Snapshot(body, air, heat, insulation, Climate.globalDrop(level), Climate.stormIntensity(level));
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        boolean exempt = player.isCreative() || player.isSpectator();

        if (player.tickCount % 20 == 0) {
            if (exempt) {
                player.setData(ModAttachments.BODY_TEMP.get(), NORMAL);
                send(player, NORMAL, (byte) 0);
            } else {
                updateSecond(player);
            }
        }
        if (!exempt) {
            applyFrostVisual(player);
        }
    }

    private static void updateSecond(ServerPlayer player) {
        Snapshot snap = sample(player);
        float temp = snap.body();
        float effective = snap.effective();
        float comfort = Config.COMFORT.get().floatValue();
        byte trend;

        if (effective < comfort) {
            float deficit = comfort - effective;
            temp -= deficit * 0.002F;
            trend = -1;
        } else {
            float rate = Math.min(0.15F, 0.03F + 0.002F * (effective - comfort));
            float before = temp;
            temp = Math.min(NORMAL, temp + rate);
            trend = temp > before ? (byte) 1 : (byte) 0;
        }
        temp = Mth.clamp(temp, MIN, NORMAL);
        player.setData(ModAttachments.BODY_TEMP.get(), temp);

        // --- Последствия ---
        if (temp < CHILLED) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, true, false, true));
            player.causeFoodExhaustion(0.05F); // на морозе быстрее голодаешь
        }
        if (temp < FREEZING) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, true, false, true));
        }
        if (temp < FROSTBITE && player.tickCount % 40 == 0) {
            float damage = temp < FROSTBITE - 2.0F ? 2.0F : 1.0F;
            player.hurt(player.serverLevel().damageSources().freeze(), damage);
        }

        send(player, temp, trend);
    }

    /** Иней на экране: чем холоднее тело, тем сильнее (как в снежной пещере). */
    private static void applyFrostVisual(ServerPlayer player) {
        float temp = player.getData(ModAttachments.BODY_TEMP.get());
        if (temp >= CHILLED) {
            return;
        }
        float k = Mth.clamp((CHILLED - temp) / 5.0F, 0.0F, 0.99F);
        int target = (int) (player.getTicksRequiredToFreeze() * k);
        if (player.getTicksFrozen() < target) {
            player.setTicksFrozen(target);
        }
    }

    private static void send(ServerPlayer player, float temp, byte trend) {
        float storm = Climate.stormIntensity(player.serverLevel());
        PacketDistributor.sendToPlayer(player, new TempSyncPayload(temp, trend, storm));
    }

    private TemperatureManager() {}
}

package com.frostbound.client;

import com.frostbound.Frostbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/** Вся клиентская логика. Класс подгружается только на клиенте. */
public final class ClientSetup {
    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onFog);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onFogColor);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(Frostbound.MODID, "thermometer"),
                ThermometerLayer::render);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null) {
            ClientTempData.reset();
            return;
        }
        if (mc.isPaused()) {
            return;
        }
        ClientTempData.tickSmooth();

        float storm = ClientTempData.displayStorm;
        if (storm < 0.05F || !level.canSeeSky(player.blockPosition())) {
            return;
        }
        // Метель: снежинки, летящие по ветру
        RandomSource random = level.random;
        int count = (int) (14 * storm);
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 30.0;
            double y = player.getY() + random.nextDouble() * 12.0 - 2.0;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 30.0;
            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                    -0.25 - random.nextDouble() * 0.1, -0.05, 0.06);
        }
    }

    /** Видимость в бурю падает почти до нуля. */
    private static void onFog(ViewportEvent.RenderFog event) {
        float storm = ClientTempData.displayStorm;
        if (storm <= 0.02F || event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null
                || player.isEyeInFluid(FluidTags.WATER) || player.isEyeInFluid(FluidTags.LAVA)) {
            return;
        }
        float exposure = mc.level.canSeeSky(player.blockPosition()) ? 1.0F : 0.35F;
        float k = storm * exposure;
        event.setNearPlaneDistance(Mth.lerp(k, event.getNearPlaneDistance(), 0.0F));
        event.setFarPlaneDistance(Mth.lerp(k, event.getFarPlaneDistance(), 28.0F));
        event.setCanceled(true);
    }

    private static void onFogColor(ViewportEvent.ComputeFogColor event) {
        float storm = ClientTempData.displayStorm;
        if (storm <= 0.02F) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }
        float exposure = mc.level.canSeeSky(player.blockPosition()) ? 1.0F : 0.35F;
        float k = storm * exposure * 0.85F;
        event.setRed(Mth.lerp(k, event.getRed(), 0.80F));
        event.setGreen(Mth.lerp(k, event.getGreen(), 0.85F));
        event.setBlue(Mth.lerp(k, event.getBlue(), 0.92F));
    }

    private ClientSetup() {}
}

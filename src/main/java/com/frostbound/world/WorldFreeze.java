package com.frostbound.world;

import com.frostbound.Config;
import com.frostbound.temperature.Climate;
import com.frostbound.temperature.HeatSources;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Мир замерзает вокруг игроков: снег копится слоями, вода покрывается льдом,
 * посевы под открытым небом без тепла гибнут. Работает случайной выборкой колонн
 * (как случайные тики Minecraft), поэтому почти не нагружает сервер.
 */
public final class WorldFreeze {
    private static final float CROP_DEATH_CHANCE = 0.15F;
    private static final float ICE_CHANCE = 0.5F;
    private static boolean stormWasActive = false;

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (level.getGameTime() % 20L == 0L) {
            announceStorm(level);
        }
        sampleWorld(level);
    }

    private static void announceStorm(ServerLevel level) {
        boolean active = Climate.stormIntensity(level) > 0.0F;
        if (active != stormWasActive) {
            stormWasActive = active;
            Component msg = Component.translatable(active ? "message.frostbound.storm_start" : "message.frostbound.storm_end")
                    .withStyle(active ? ChatFormatting.AQUA : ChatFormatting.GRAY);
            for (ServerPlayer player : level.players()) {
                player.displayClientMessage(msg, false);
            }
        }
    }

    private static void sampleWorld(ServerLevel level) {
        int samples = Config.WORLD_SAMPLES_PER_TICK.get();
        if (samples <= 0) {
            return;
        }
        double drop = Climate.globalDrop(level);
        float storm = Climate.stormIntensity(level);
        if (drop <= 0.0 && storm <= 0.0F) {
            return;
        }
        int radius = Config.WORLD_RADIUS.get();
        float stormCold = Config.STORM_COLD.get().floatValue();
        RandomSource random = level.random;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }
            for (int i = 0; i < samples; i++) {
                int x = player.getBlockX() + random.nextInt(radius * 2 + 1) - radius;
                int z = player.getBlockZ() + random.nextInt(radius * 2 + 1) - radius;
                BlockPos column = new BlockPos(x, 0, z);
                if (!level.hasChunkAt(column)) {
                    continue;
                }
                BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column);
                float biomeTemp = level.getBiome(top).value().getBaseTemperature();
                float local = 5.0F + biomeTemp * 15.0F - (float) drop - storm * stormCold;
                if (local > 0.0F) {
                    continue; // здесь ещё не достаточно холодно
                }
                if (level.getBrightness(LightLayer.BLOCK, top) >= 10) {
                    continue; // рядом тёплый источник света
                }
                freezeColumn(level, top, storm, random);
            }
        }
    }

    private static void freezeColumn(ServerLevel level, BlockPos top, float storm, RandomSource random) {
        BlockPos below = top.below();
        BlockState belowState = level.getBlockState(below);
        BlockState topState = level.getBlockState(top);

        // 1. Вода -> лёд
        if (Config.WATER_FREEZING.get() && belowState.is(Blocks.WATER)
                && level.getFluidState(below).isSource() && random.nextFloat() < ICE_CHANCE) {
            level.setBlockAndUpdate(below, Blocks.ICE.defaultBlockState());
            return;
        }

        // 2. Посевы гибнут, если рядом нет тепла
        if (Config.CROP_DEATH.get()
                && (topState.getBlock() instanceof CropBlock || topState.getBlock() instanceof StemBlock)) {
            if (random.nextFloat() < CROP_DEATH_CHANCE && HeatSources.heatAt(level, top) < 2.0F) {
                level.destroyBlock(top, false);
            }
            return;
        }

        // 3. Снег копится слоями
        if (Config.SNOW_ACCUMULATION.get()) {
            int cap = storm > 0.3F ? Config.MAX_SNOW_LAYERS.get() : Math.min(2, Config.MAX_SNOW_LAYERS.get());
            if (belowState.is(Blocks.SNOW)) {
                addLayer(level, below, belowState, cap);
            } else if (topState.is(Blocks.SNOW)) {
                addLayer(level, top, topState, cap);
            } else if (topState.isAir()) {
                BlockState snow = Blocks.SNOW.defaultBlockState();
                if (snow.canSurvive(level, top)) {
                    level.setBlockAndUpdate(top, snow);
                }
            }
        }
    }

    private static void addLayer(ServerLevel level, BlockPos pos, BlockState state, int cap) {
        int layers = state.getValue(SnowLayerBlock.LAYERS);
        if (layers < cap) {
            level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers + 1));
        }
    }

    private WorldFreeze() {}
}

package com.frostbound.temperature;

import com.frostbound.block.CoalHeaterBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Считает, сколько градусов добавляют окружающие источники тепла. */
public final class HeatSources {
    private static final int H_RADIUS = 8;
    private static final int V_RADIUS = 5;
    private static final float MAX_STRONG = 40.0F;
    private static final float MAX_WEAK = 4.0F; // факелы и фонари не должны заменять обогреватель

    public static float heatAt(Level level, BlockPos center) {
        float strong = 0.0F;
        float weak = 0.0F;
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();

        for (int dx = -H_RADIUS; dx <= H_RADIUS; dx++) {
            for (int dy = -V_RADIUS; dy <= V_RADIUS; dy++) {
                for (int dz = -H_RADIUS; dz <= H_RADIUS; dz++) {
                    p.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState s = level.getBlockState(p);
                    if (s.isAir()) {
                        continue;
                    }
                    float power = power(s);
                    if (power <= 0.0F) {
                        continue;
                    }
                    float radius = radius(s);
                    float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist > radius) {
                        continue;
                    }
                    float heat = power * (1.0F - dist / (radius + 1.0F));
                    if (isWeak(s)) {
                        weak += heat;
                    } else {
                        strong += heat;
                    }
                }
            }
        }
        return Math.min(strong, MAX_STRONG) + Math.min(weak, MAX_WEAK);
    }

    private static boolean isWeak(BlockState s) {
        return s.is(Blocks.TORCH) || s.is(Blocks.WALL_TORCH) || s.is(Blocks.LANTERN)
                || s.is(Blocks.SOUL_TORCH) || s.is(Blocks.SOUL_WALL_TORCH) || s.is(Blocks.SOUL_LANTERN);
    }

    /** Максимальный прирост температуры вплотную к источнику, °C. */
    private static float power(BlockState s) {
        Block b = s.getBlock();
        if (b instanceof CoalHeaterBlock) {
            return s.getValue(CoalHeaterBlock.LIT) ? 22.0F : 0.0F;
        }
        if (b instanceof CampfireBlock) {
            if (!s.getValue(CampfireBlock.LIT)) {
                return 0.0F;
            }
            return s.is(Blocks.SOUL_CAMPFIRE) ? 8.0F : 12.0F;
        }
        if (b instanceof AbstractFurnaceBlock) {
            return s.getValue(AbstractFurnaceBlock.LIT) ? 9.0F : 0.0F;
        }
        if (s.is(Blocks.LAVA)) {
            return 14.0F;
        }
        if (b instanceof BaseFireBlock) {
            return s.is(Blocks.SOUL_FIRE) ? 5.0F : 8.0F;
        }
        if (s.is(Blocks.MAGMA_BLOCK)) {
            return 4.0F;
        }
        if (isWeak(s)) {
            return 2.0F;
        }
        return 0.0F;
    }

    /** Радиус действия источника в блоках. */
    private static float radius(BlockState s) {
        Block b = s.getBlock();
        if (b instanceof CoalHeaterBlock) {
            return 8.0F;
        }
        if (b instanceof CampfireBlock) {
            return 5.0F;
        }
        if (b instanceof AbstractFurnaceBlock || s.is(Blocks.LAVA)) {
            return 4.0F;
        }
        return 3.0F;
    }

    private HeatSources() {}
}

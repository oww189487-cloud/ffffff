package com.frostbound.temperature;

import com.frostbound.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * Климат мира. Всё считается детерминированно от игрового времени (dayTime),
 * поэтому не нужно хранить состояние: сон и команды /time автоматически двигают зиму вперёд.
 */
public final class Climate {
    public static final long DAY = 24000L;

    /** Насколько мир остыл относительно начала игры, °C (>= 0). */
    public static double globalDrop(Level level) {
        if (level.dimension() != Level.OVERWORLD) {
            return 0.0;
        }
        double day = level.getDayTime() / (double) DAY;
        double progress = Math.max(0.0, day - Config.GRACE_DAYS.get());
        return Math.min(Config.MAX_DROP.get(), progress * Config.DROP_PER_DAY.get());
    }

    /** Сила ледяной бури 0..1. Буря занимает последние stormDurationTicks каждого цикла. */
    public static float stormIntensity(Level level) {
        if (level.dimension() != Level.OVERWORLD) {
            return 0.0F;
        }
        long time = level.getDayTime();
        if (time < Config.GRACE_DAYS.get() * DAY) {
            return 0.0F;
        }
        long cycle = Config.STORM_PERIOD_DAYS.get() * DAY;
        long length = Math.min(Config.STORM_DURATION.get(), cycle);
        long phase = time % cycle;
        long start = cycle - length;
        if (phase < start) {
            return 0.0F;
        }
        float ramp = 600.0F; // плавное нарастание и затухание (30 секунд)
        float in = (phase - start) / ramp;
        float out = (cycle - phase) / ramp;
        return Mth.clamp(Math.min(in, out), 0.0F, 1.0F);
    }

    /** 0 днём, 1 в полночь. */
    public static float nightFactor(Level level) {
        long t = level.getDayTime() % DAY;
        if (t < 12000L) {
            return 0.0F;
        }
        return (float) Math.sin(Math.PI * (t - 12000L) / 12000.0);
    }

    /** Температура воздуха в точке без учёта источников тепла, °C. */
    public static float air(Level level, BlockPos pos) {
        if (level.dimension() == Level.NETHER) {
            return 30.0F;
        }
        if (level.dimension() != Level.OVERWORLD) {
            return 15.0F;
        }
        float biomeTemp = level.getBiome(pos).value().getBaseTemperature();
        float t = 5.0F + biomeTemp * 15.0F;                    // равнина ~17°C, тундра ~5°C, пустыня ~35°C
        t -= Math.max(0, pos.getY() - 80) * 0.05F;             // выше — холоднее
        t -= (float) globalDrop(level);

        if (level.canSeeSky(pos)) {
            t -= nightFactor(level) * Config.NIGHT_COLD.get().floatValue();
            t -= stormIntensity(level) * Config.STORM_COLD.get().floatValue();
        } else if (t < 10.0F) {
            t += (10.0F - t) * 0.4F;                           // крыша и стены держат часть тепла
        }
        return t;
    }

    private Climate() {}
}

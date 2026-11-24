package com.frostbound;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Все настройки мода. Файл: config/frostbound-common.toml */
public final class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    // ---------- Наступление зимы ----------
    public static final ModConfigSpec.IntValue GRACE_DAYS = B
            .comment("Сколько дней мир ещё тёплый (потом начинается похолодание)")
            .defineInRange("graceDays", 2, 0, 1000);

    public static final ModConfigSpec.DoubleValue DROP_PER_DAY = B
            .comment("На сколько градусов падает температура мира за каждый игровой день")
            .defineInRange("dropPerDay", 1.5, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue MAX_DROP = B
            .comment("Максимальное глобальное похолодание, °C")
            .defineInRange("maxDrop", 45.0, 0.0, 200.0);

    public static final ModConfigSpec.DoubleValue NIGHT_COLD = B
            .comment("Дополнительный холод ночью под открытым небом, °C")
            .defineInRange("nightExtraCold", 6.0, 0.0, 50.0);

    // ---------- Ледяные бури ----------
    public static final ModConfigSpec.IntValue STORM_PERIOD_DAYS = B
            .comment("Раз в сколько дней приходит ледяная буря")
            .defineInRange("stormPeriodDays", 7, 1, 1000);

    public static final ModConfigSpec.IntValue STORM_DURATION = B
            .comment("Длительность бури в тиках (24000 тиков = 1 игровые сутки)")
            .defineInRange("stormDurationTicks", 12000, 600, 240000);

    public static final ModConfigSpec.DoubleValue STORM_COLD = B
            .comment("Дополнительный холод во время бури под открытым небом, °C")
            .defineInRange("stormExtraCold", 18.0, 0.0, 100.0);

    // ---------- Тело игрока ----------
    public static final ModConfigSpec.DoubleValue COMFORT = B
            .comment("Эффективная температура воздуха (с учётом тепла и одежды), ниже которой тело остывает")
            .defineInRange("comfortTemperature", 15.0, -50.0, 50.0);

    // ---------- Мир ----------
    public static final ModConfigSpec.BooleanValue SNOW_ACCUMULATION = B
            .comment("Снег накапливается слоями вокруг игроков")
            .define("snowAccumulation", true);

    public static final ModConfigSpec.IntValue MAX_SNOW_LAYERS = B
            .comment("Максимальная высота сугробов во время бури (1-8)")
            .defineInRange("maxSnowLayers", 6, 1, 8);

    public static final ModConfigSpec.BooleanValue WATER_FREEZING = B
            .comment("Вода замерзает, когда достаточно холодно")
            .define("waterFreezing", true);

    public static final ModConfigSpec.BooleanValue CROP_DEATH = B
            .comment("Посевы под открытым небом без источника тепла гибнут")
            .define("cropDeath", true);

    public static final ModConfigSpec.IntValue WORLD_SAMPLES_PER_TICK = B
            .comment("Сколько случайных колонн блоков проверяется за тик возле каждого игрока")
            .defineInRange("worldSamplesPerTick", 6, 0, 100);

    public static final ModConfigSpec.IntValue WORLD_RADIUS = B
            .comment("Радиус вокруг игрока, в котором мир замерзает")
            .defineInRange("worldRadius", 48, 8, 128);

    public static final ModConfigSpec SPEC = B.build();

    private Config() {}
}

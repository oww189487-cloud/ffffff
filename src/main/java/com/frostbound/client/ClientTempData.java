package com.frostbound.client;

/** Данные, присланные сервером. Намеренно без импортов клиентских классов Minecraft. */
public final class ClientTempData {
    public static float temp = 36.6F;
    public static byte trend = 0;
    /** Целевая сила бури от сервера и плавно догоняющее значение для отрисовки. */
    public static float targetStorm = 0.0F;
    public static float displayStorm = 0.0F;

    public static void update(float newTemp, byte newTrend, float newStorm) {
        temp = newTemp;
        trend = newTrend;
        targetStorm = newStorm;
    }

    public static void tickSmooth() {
        displayStorm += (targetStorm - displayStorm) * 0.03F;
        if (Math.abs(targetStorm - displayStorm) < 0.001F) {
            displayStorm = targetStorm;
        }
    }

    public static void reset() {
        temp = 36.6F;
        trend = 0;
        targetStorm = 0.0F;
        displayStorm = 0.0F;
    }

    private ClientTempData() {}
}

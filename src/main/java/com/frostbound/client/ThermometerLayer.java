package com.frostbound.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/** Иконка-термометр слева от полоски здоровья. Рисуется прямоугольниками, без текстур. */
public final class ThermometerLayer {
    private static final int GLASS = 0xFF9A9A9A;
    private static final int TUBE_BG = 0xFF1E1E1E;

    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return;
        }
        if (mc.player.isCreative() || mc.player.isSpectator()) {
            return;
        }

        float temp = ClientTempData.temp;
        float frac = Mth.clamp((temp - 30.0F) / (36.6F - 30.0F), 0.0F, 1.0F);
        int color = lerpColor(0x55AAFF, 0xFF5533, frac);

        int x = g.guiWidth() / 2 - 91 - 14;
        int y = g.guiHeight() - 41;

        // Дрожь, когда совсем холодно
        if (temp < 33.0F && (mc.level.getGameTime() / 2) % 2 == 0) {
            x += 1;
        }

        // Рамка меняет цвет по тренду: остывает — голубая, согревается — оранжевая
        int border = GLASS;
        if (ClientTempData.trend < 0) {
            border = 0xFF8FD0FF;
        } else if (ClientTempData.trend > 0) {
            border = 0xFFFFB066;
        }

        // Трубка
        g.fill(x + 2, y, x + 7, y + 10, border);
        g.fill(x + 3, y + 1, x + 6, y + 9, TUBE_BG);
        // Колба
        g.fill(x + 1, y + 9, x + 8, y + 15, border);
        g.fill(x + 2, y + 10, x + 7, y + 14, 0xFF000000 | color);
        // Столбик ртути
        int h = Math.max(1, Math.round(8 * frac));
        g.fill(x + 3, y + 9 - h, x + 6, y + 9, 0xFF000000 | color);
    }

    private static int lerpColor(int a, int b, float t) {
        int r = (int) Mth.lerp(t, (a >> 16) & 0xFF, (b >> 16) & 0xFF);
        int gr = (int) Mth.lerp(t, (a >> 8) & 0xFF, (b >> 8) & 0xFF);
        int bl = (int) Mth.lerp(t, a & 0xFF, b & 0xFF);
        return (r << 16) | (gr << 8) | bl;
    }

    private ThermometerLayer() {}
}

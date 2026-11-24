package com.frostbound.temperature;

import com.frostbound.item.WarmClothingItem;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Сколько градусов «добавляет» одежда игрока. */
public final class Insulation {
    public static float total(Player player) {
        float sum = 0.0F;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof WarmClothingItem warm) {
                sum += warm.getInsulation();
            } else if (stack.is(Items.LEATHER_HELMET)) {
                sum += 0.5F;
            } else if (stack.is(Items.LEATHER_CHESTPLATE)) {
                sum += 1.5F;
            } else if (stack.is(Items.LEATHER_LEGGINGS)) {
                sum += 1.0F;
            } else if (stack.is(Items.LEATHER_BOOTS)) {
                sum += 0.5F;
            }
        }
        return sum;
    }

    private Insulation() {}
}

package com.frostbound.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Тёплая одежда: обычная броня, но с утеплением (°C к эффективной температуре воздуха). */
public class WarmClothingItem extends ArmorItem {
    private final float insulation;

    public WarmClothingItem(Holder<ArmorMaterial> material, ArmorItem.Type type, float insulation, Properties props) {
        super(material, type, props);
        this.insulation = insulation;
    }

    public float getInsulation() {
        return insulation;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.frostbound.insulation", String.valueOf(insulation))
                .withStyle(ChatFormatting.AQUA));
    }
}

package com.frostbound.registry;

import com.frostbound.Frostbound;
import com.frostbound.item.WarmClothingItem;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Frostbound.MODID);

    public static final DeferredItem<BlockItem> COAL_HEATER = ITEMS.register("coal_heater",
            () -> new BlockItem(ModBlocks.COAL_HEATER.get(), new Item.Properties()));

    // Множитель прочности 10 (кожаная броня = 5, железная = 15)
    public static final DeferredItem<WarmClothingItem> WARM_HOOD = ITEMS.register("warm_hood",
            () -> new WarmClothingItem(ModArmorMaterials.WARM_CLOTHING, ArmorItem.Type.HELMET, 2.0F,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(10))));

    public static final DeferredItem<WarmClothingItem> WARM_COAT = ITEMS.register("warm_coat",
            () -> new WarmClothingItem(ModArmorMaterials.WARM_CLOTHING, ArmorItem.Type.CHESTPLATE, 5.0F,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(10))));

    public static final DeferredItem<WarmClothingItem> WARM_PANTS = ITEMS.register("warm_pants",
            () -> new WarmClothingItem(ModArmorMaterials.WARM_CLOTHING, ArmorItem.Type.LEGGINGS, 3.5F,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(10))));

    public static final DeferredItem<WarmClothingItem> WARM_BOOTS = ITEMS.register("warm_boots",
            () -> new WarmClothingItem(ModArmorMaterials.WARM_CLOTHING, ArmorItem.Type.BOOTS, 2.0F,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(10))));

    private ModItems() {}
}

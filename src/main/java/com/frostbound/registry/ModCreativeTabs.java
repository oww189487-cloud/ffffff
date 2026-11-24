package com.frostbound.registry;

import java.util.function.Supplier;

import com.frostbound.Frostbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Frostbound.MODID);

    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.frostbound"))
                    .icon(() -> new ItemStack(ModItems.COAL_HEATER.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.COAL_HEATER.get());
                        output.accept(ModItems.WARM_HOOD.get());
                        output.accept(ModItems.WARM_COAT.get());
                        output.accept(ModItems.WARM_PANTS.get());
                        output.accept(ModItems.WARM_BOOTS.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}

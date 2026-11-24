package com.frostbound.registry;

import java.util.function.Supplier;

import com.frostbound.Frostbound;
import com.frostbound.block.CoalHeaterBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Frostbound.MODID);

    public static final Supplier<BlockEntityType<CoalHeaterBlockEntity>> COAL_HEATER =
            BLOCK_ENTITIES.register("coal_heater",
                    () -> BlockEntityType.Builder.of(CoalHeaterBlockEntity::new, ModBlocks.COAL_HEATER.get()).build(null));

    private ModBlockEntities() {}
}

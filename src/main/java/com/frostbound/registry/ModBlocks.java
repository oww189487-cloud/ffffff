package com.frostbound.registry;

import com.frostbound.Frostbound;
import com.frostbound.block.CoalHeaterBlock;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Frostbound.MODID);

    public static final DeferredBlock<CoalHeaterBlock> COAL_HEATER = BLOCKS.register("coal_heater",
            () -> new CoalHeaterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)));

    private ModBlocks() {}
}

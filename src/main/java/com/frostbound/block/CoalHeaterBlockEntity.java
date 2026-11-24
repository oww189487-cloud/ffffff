package com.frostbound.block;

import com.frostbound.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoalHeaterBlockEntity extends BlockEntity {
    /** 24000 тиков = 20 минут горения. Уголь даёт 1600 тиков. */
    public static final int MAX_FUEL = 24000;

    private int fuel;

    public CoalHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_HEATER.get(), pos, state);
    }

    public int getFuel() {
        return fuel;
    }

    public void addFuel(int ticks) {
        fuel = Math.min(MAX_FUEL, fuel + ticks);
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CoalHeaterBlockEntity heater) {
        boolean lit = state.getValue(CoalHeaterBlock.LIT);
        if (heater.fuel > 0) {
            heater.fuel--;
            if (!lit) {
                level.setBlock(pos, state.setValue(CoalHeaterBlock.LIT, true), 3);
            }
            if (heater.fuel % 200 == 0) {
                heater.setChanged();
            }
        } else if (lit) {
            level.setBlock(pos, state.setValue(CoalHeaterBlock.LIT, false), 3);
            heater.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Fuel", fuel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuel = tag.getInt("Fuel");
    }
}

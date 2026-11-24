package com.frostbound.block;

import javax.annotation.Nullable;

import com.frostbound.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Обогреватель на угле. ПКМ с топливом (уголь, дрова...) — загрузить топливо.
 * ПКМ пустой рукой — показать, сколько топлива осталось.
 */
public class CoalHeaterBlock extends BaseEntityBlock {
    public static final MapCodec<CoalHeaterBlock> CODEC = simpleCodec(CoalHeaterBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CoalHeaterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // BaseEntityBlock по умолчанию делает блок невидимым
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoalHeaterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.COAL_HEATER.get(), CoalHeaterBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() || stack.is(Items.LAVA_BUCKET)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int burnTime = stack.getBurnTime(RecipeType.SMELTING);
        if (burnTime <= 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CoalHeaterBlockEntity heater) {
            if (heater.getFuel() + burnTime > CoalHeaterBlockEntity.MAX_FUEL) {
                player.displayClientMessage(Component.translatable("message.frostbound.heater_full"), true);
            } else {
                heater.addFuel(burnTime);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (!state.getValue(LIT)) {
                    level.setBlock(pos, state.setValue(LIT, true), 3);
                }
                showFuel(player, heater);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CoalHeaterBlockEntity heater) {
            showFuel(player, heater);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void showFuel(Player player, CoalHeaterBlockEntity heater) {
        String minutes = String.format("%.1f", heater.getFuel() / 1200.0F);
        player.displayClientMessage(Component.translatable("message.frostbound.heater_fuel", minutes), true);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                    0.0, 0.03, 0.0);
        }
        if (random.nextInt(8) == 0) {
            level.addParticle(ParticleTypes.FLAME,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    pos.getY() + 0.3 + random.nextDouble() * 0.4,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.0, 0.0);
        }
    }
}

package net.svartmagi.chunkload;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.svartmagi.SvartmagiConfig;

/**
 * Simple chunk loader: right-click toggles loading the chunk it sits in.
 * Active loaders keep their chunk ticking even with no players nearby.
 */
public class ChunkLoaderBlock extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public ChunkLoaderBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkLoaderBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity loader) {
            loader.setActive(true);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!SvartmagiConfig.CHUNK_LOADER_ENABLED.get()) {
            if (!level.isClientSide && player instanceof ServerPlayer sp) {
                sp.displayClientMessage(Component.translatable("message.svartmagi.chunk_loader_disabled"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity loader) {
            loader.setActive(!loader.isActive());
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        Component.translatable(loader.isActive()
                                ? "message.svartmagi.chunk_loader_enabled"
                                : "message.svartmagi.chunk_loader_disabled_pos"),
                        true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity loader) {
            loader.setActive(false);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

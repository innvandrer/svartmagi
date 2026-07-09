package net.svartmagi.tech;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.registry.ModBlockEntities;

/**
 * Kraftkabel: aksebasert blokk (som en stokk) som flytter FE mellom
 * maskiner over avstand, i stedet for kun direkte nabokontakt.
 */
public class CableBlock extends RotatedPillarBlock implements EntityBlock {
    public CableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.KRAFTKABEL.get()) return null;
        return (BlockEntityTicker<T>) (BlockEntityTicker<CableBlockEntity>) (lvl, pos, st, be) -> be.serverTick();
    }
}

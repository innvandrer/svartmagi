package net.svartmagi.chunkload;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.registry.ModBlockEntities;

/** Keeps the host chunk loaded while active (server-side tickets). */
public class ChunkLoaderBlockEntity extends BlockEntity {
    private boolean active = true;

    public ChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNKLASTER.get(), pos, state);
    }

    public boolean isActive() {
        return active;
    }

    public boolean shouldForceChunk() {
        return active && SvartmagiConfig.CHUNK_LOADER_ENABLED.get();
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        setChanged();
        if (level != null && !level.isClientSide) {
            syncBlockState();
            updateChunkTicket();
        }
    }

    private void syncBlockState() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (state.getValue(ChunkLoaderBlock.ACTIVE) != active) {
            level.setBlockAndUpdate(worldPosition, state.setValue(ChunkLoaderBlock.ACTIVE, active));
        }
    }

    private void updateChunkTicket() {
        if (level == null || level.isClientSide) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        ChunkPos chunkPos = new ChunkPos(worldPosition);
        boolean load = shouldForceChunk();
        ChunkLoaderTickets.CONTROLLER.forceChunk(serverLevel, worldPosition, chunkPos.x, chunkPos.z, load, true);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            syncBlockState();
            updateChunkTicket();
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            ChunkPos chunkPos = new ChunkPos(worldPosition);
            ChunkLoaderTickets.CONTROLLER.forceChunk(serverLevel, worldPosition, chunkPos.x, chunkPos.z, false, true);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Active", active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = !tag.contains("Active") || tag.getBoolean("Active");
    }
}

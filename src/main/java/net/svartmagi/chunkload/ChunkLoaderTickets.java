package net.svartmagi.chunkload;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.svartmagi.Svartmagi;

/** NeoForge ticket controller for Svartmagi chunk loaders. */
public final class ChunkLoaderTickets {
    public static final TicketController CONTROLLER = new TicketController(
            Svartmagi.id("chunk_loader"),
            (level, helper) -> helper.getBlockTickets().forEach((pos, tickets) -> {
                if (!level.isLoaded(pos)) {
                    return;
                }
                BlockEntity be = level.getBlockEntity(pos);
                if (!(be instanceof ChunkLoaderBlockEntity loader) || !loader.shouldForceChunk()) {
                    helper.removeAllTickets(pos);
                }
            }));

    private ChunkLoaderTickets() {}
}

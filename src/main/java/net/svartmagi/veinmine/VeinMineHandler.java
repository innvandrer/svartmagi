package net.svartmagi.veinmine;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.registry.ModTags;

/**
 * FTB Ultimine-lignende veinmining: hold tasten og bryt en blokk i
 * "veinmineable"-taggen, saa brytes hele aaren/treet (BFS, begrenset av
 * config). Aktiv tilstand per spiller settes av VeinMinePayload.
 */
public final class VeinMineHandler {
    private static final Set<UUID> ACTIVE = new HashSet<>();
    private static final ThreadLocal<Boolean> VEINMINING = ThreadLocal.withInitial(() -> false);

    public static void setActive(UUID player, boolean active) {
        if (active) {
            ACTIVE.add(player);
        } else {
            ACTIVE.remove(player);
        }
    }

    public static void onPlayerLeave(UUID player) {
        ACTIVE.remove(player);
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!SvartmagiConfig.VEINMINE_ENABLED.get()) return;
        if (VEINMINING.get()) return; // re-entrans fra vaare egne destroyBlock-kall
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!ACTIVE.contains(player.getUUID())) return;
        if (player.isCreative()) return;

        BlockState state = event.getState();
        if (!state.is(ModTags.VEINMINEABLE)) return;

        ItemStack tool = player.getMainHandItem();
        if (SvartmagiConfig.VEINMINE_REQUIRE_TOOL.get() && !canHarvest(player, state, tool)) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos origin = event.getPos();
        Block targetBlock = state.getBlock();
        int maxBlocks = SvartmagiConfig.VEINMINE_MAX_BLOCKS.get();

        // BFS over identiske naboblokker (26-veis)
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin);
        visited.add(origin);
        int broken = 0;

        VEINMINING.set(true);
        try {
            while (!queue.isEmpty() && broken < maxBlocks) {
                BlockPos pos = queue.poll();
                if (!pos.equals(origin)) {
                    if (!level.isLoaded(pos)) continue;
                    BlockState current = level.getBlockState(pos);
                    if (!current.is(targetBlock)) continue;
                    if (tool.isEmpty() && SvartmagiConfig.VEINMINE_REQUIRE_TOOL.get()) break;
                    if (!player.gameMode.destroyBlock(pos)) continue;
                    player.causeFoodExhaustion(SvartmagiConfig.VEINMINE_EXHAUSTION_PER_BLOCK.get().floatValue());
                    broken++;
                    tool = player.getMainHandItem();
                }
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            BlockPos next = pos.offset(dx, dy, dz);
                            if (visited.size() < maxBlocks * 8 && visited.add(next)
                                    && level.getBlockState(next).is(targetBlock)) {
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        } finally {
            VEINMINING.set(false);
        }
    }

    private static boolean canHarvest(ServerPlayer player, BlockState state, ItemStack tool) {
        return !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
    }

    private VeinMineHandler() {}
}

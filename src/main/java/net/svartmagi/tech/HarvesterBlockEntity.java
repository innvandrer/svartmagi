package net.svartmagi.tech;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.item.UpgradeItem;
import net.svartmagi.menu.HarvesterMenu;
import net.svartmagi.registry.ModBlockEntities;

/**
 * Hoester avlinger og trær i et omraade foran maskinen.
 *
 * Ytelse: ingen full omraade-skanning per tick. En markoer gaar gjennom
 * omraadet med et begrenset antall blokksjekker per intervall, og maks en
 * hoeste-operasjon utfoeres per intervall.
 */
public class HarvesterBlockEntity extends MachineBlockEntity {
    private static final int CHECKS_PER_OPERATION = 8;
    private static final int MAX_TREE_BLOCKS = 96;

    private int cooldown;
    private int cursor;
    @Nullable
    private List<BlockPos> areaCache;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 2;
        }
    };

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INNHOSTER.get(), pos, state, 9);
    }

    @Override
    protected int maxUpgrades(UpgradeItem.Kind kind) {
        return kind == UpgradeItem.Kind.SPEED ? 3 : 0;
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        // Slots er output + sapling-buffer; alt tillatt via automatikk.
        return true;
    }

    private List<BlockPos> area() {
        if (areaCache == null) {
            int radius = SvartmagiConfig.HARVESTER_RADIUS.get();
            Direction facing = getBlockState().getValue(MachineBlock.FACING);
            BlockPos center = worldPosition.relative(facing, radius + 1);
            List<BlockPos> positions = new ArrayList<>((2 * radius + 1) * (2 * radius + 1));
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    positions.add(center.offset(dx, 0, dz));
                }
            }
            areaCache = positions;
        }
        return areaCache;
    }

    /** Kalles ved config-endring/rotasjon for aa bygge omraadet paa nytt. */
    public void invalidateArea() {
        areaCache = null;
        cursor = 0;
    }

    @Override
    public void serverTick() {
        if (!SvartmagiConfig.TECH_ENABLED.get()) return;
        if (--cooldown > 0) return;
        cooldown = Math.max(5, SvartmagiConfig.HARVESTER_INTERVAL_TICKS.get() / (1 + speedUpgrades));

        int cost = SvartmagiConfig.HARVESTER_FE_PER_OPERATION.get();
        if (energy.getEnergyStored() < cost) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        List<BlockPos> positions = area();
        if (positions.isEmpty()) return;

        for (int i = 0; i < CHECKS_PER_OPERATION; i++) {
            BlockPos pos = positions.get(cursor);
            cursor = (cursor + 1) % positions.size();
            if (!serverLevel.isLoaded(pos)) continue;

            if (tryHarvest(serverLevel, pos)) {
                energy.consume(cost);
                pushOutput();
                return;
            }
        }
        pushOutput();
    }

    private boolean tryHarvest(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);

        // Modne avlinger: hoest + replant
        if (state.getBlock() instanceof CropBlock crop) {
            if (!crop.isMaxAge(state)) return false;
            collectDrops(serverLevel, pos, state);
            serverLevel.setBlock(pos, crop.getStateForAge(0), 3);
            return true;
        }
        if (state.getBlock() instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) >= 3) {
            collectDrops(serverLevel, pos, state);
            serverLevel.setBlock(pos, state.setValue(NetherWartBlock.AGE, 0), 3);
            return true;
        }

        // Trestamme: fell hele treet (begrenset BFS), replant sapling
        if (state.is(BlockTags.LOGS)) {
            boolean felled = fellTree(serverLevel, pos);
            if (felled) {
                tryReplantSapling(serverLevel, pos);
            }
            return felled;
        }

        // Tom jordflate under: replant sapling fra bufferen
        if (serverLevel.isEmptyBlock(pos)
                && serverLevel.getBlockState(pos.below()).is(BlockTags.DIRT)) {
            return tryReplantSapling(serverLevel, pos);
        }
        return false;
    }

    private boolean fellTree(ServerLevel serverLevel, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        List<BlockPos> logs = new ArrayList<>();

        while (!queue.isEmpty() && logs.size() < MAX_TREE_BLOCKS) {
            BlockPos pos = queue.poll();
            BlockState state = serverLevel.getBlockState(pos);
            if (!state.is(BlockTags.LOGS)) continue;
            logs.add(pos);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos next = pos.offset(dx, dy, dz);
                        if (visited.add(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
        }
        if (logs.isEmpty()) return false;

        for (BlockPos pos : logs) {
            BlockState state = serverLevel.getBlockState(pos);
            collectDrops(serverLevel, pos, state);
        }
        return true;
    }

    private boolean tryReplantSapling(ServerLevel serverLevel, BlockPos pos) {
        if (!serverLevel.isEmptyBlock(pos) || !serverLevel.getBlockState(pos.below()).is(BlockTags.DIRT)) {
            return false;
        }
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof SaplingBlock sapling) {
                BlockState saplingState = sapling.defaultBlockState();
                if (saplingState.getBlock() instanceof BushBlock && saplingState.canSurvive(serverLevel, pos)) {
                    serverLevel.setBlock(pos, saplingState, 3);
                    stack.shrink(1);
                    inventory.setStackInSlot(slot, stack);
                    return true;
                }
            }
        }
        return false;
    }

    private void collectDrops(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, serverLevel.getBlockEntity(pos));
        serverLevel.removeBlock(pos, false);
        for (ItemStack drop : drops) {
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, drop, false);
            if (!remainder.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(serverLevel,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, remainder);
            }
        }
    }

    /** Dytt hoestet innhold (unntatt saplings) til nabo-inventar. */
    private void pushOutput() {
        for (Direction dir : Direction.values()) {
            IItemHandler neighbor = itemNeighbor(dir);
            if (neighbor == null) continue;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof SaplingBlock) {
                    continue; // behold saplings til replanting
                }
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(neighbor, stack.copy(), false);
                inventory.setStackInSlot(slot, remainder);
            }
            return; // en nabo er nok
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HarvesterMenu(containerId, playerInventory, this, data);
    }
}

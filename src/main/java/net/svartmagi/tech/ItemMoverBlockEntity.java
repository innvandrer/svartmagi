package net.svartmagi.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.registry.ModBlockEntities;

/** Buffer + flyttelogikk for uttrekkeren. Nabocapabilities caches. */
public class ItemMoverBlockEntity extends BlockEntity {
    private final ItemStackHandler buffer = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private int cooldown;
    private BlockCapabilityCache<IItemHandler, Direction> sourceCache;
    private BlockCapabilityCache<IItemHandler, Direction> targetCache;

    public ItemMoverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UTTREKKER.get(), pos, state);
    }

    public IItemHandler getBuffer() {
        return buffer;
    }

    public void serverTick() {
        if (!SvartmagiConfig.TECH_ENABLED.get()) return;
        if (--cooldown > 0) return;
        cooldown = SvartmagiConfig.PIPE_INTERVAL_TICKS.get();
        if (!(level instanceof ServerLevel serverLevel)) return;

        Direction facing = getBlockState().getValue(ItemMoverBlock.FACING);
        if (sourceCache == null) {
            sourceCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel,
                    worldPosition.relative(facing.getOpposite()), facing);
            targetCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel,
                    worldPosition.relative(facing), facing.getOpposite());
        }

        int budget = SvartmagiConfig.PIPE_TRANSFER_AMOUNT.get();

        // 1) Trekk inn i bufferen fra kilden bak
        IItemHandler source = sourceCache.getCapability();
        if (source != null && buffer.getStackInSlot(0).getCount() < 64) {
            for (int slot = 0; slot < source.getSlots() && budget > 0; slot++) {
                ItemStack extracted = source.extractItem(slot, budget, true);
                if (extracted.isEmpty()) continue;
                ItemStack remainder = buffer.insertItem(0, extracted, true);
                int movable = extracted.getCount() - remainder.getCount();
                if (movable <= 0) continue;
                ItemStack moved = source.extractItem(slot, movable, false);
                buffer.insertItem(0, moved, false);
                budget -= moved.getCount();
                break;
            }
        }

        // 2) Dytt bufferen til maalet foran
        ItemStack buffered = buffer.getStackInSlot(0);
        if (!buffered.isEmpty()) {
            IItemHandler target = targetCache.getCapability();
            if (target != null) {
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(target, buffered.copy(), false);
                buffer.setStackInSlot(0, remainder);
            }
        }
    }

    public void dropContents() {
        if (level == null) return;
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                buffer.getStackInSlot(0));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", buffer.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) buffer.deserializeNBT(registries, tag.getCompound("Buffer"));
    }
}

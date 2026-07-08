package net.svartmagi.tech;

import java.util.function.IntPredicate;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Wrapper for automasjon (roer/hoppere): begrenser hvilke slots det kan
 * settes inn i og trekkes ut fra, saa maskin-input ikke "stjeles".
 */
public class FilteredItemHandler implements IItemHandler {
    private final IItemHandler delegate;
    private final IntPredicate canInsert;
    private final IntPredicate canExtract;

    public FilteredItemHandler(IItemHandler delegate, IntPredicate canInsert, IntPredicate canExtract) {
        this.delegate = delegate;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return canInsert.test(slot) ? delegate.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract.test(slot) ? delegate.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return canInsert.test(slot) && delegate.isItemValid(slot, stack);
    }
}

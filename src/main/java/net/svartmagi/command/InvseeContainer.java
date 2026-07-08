package net.svartmagi.command;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 45-slots visning av en annen spillers inventar for /invsee:
 * 0-35 hovedinventar, 36-39 rustning, 40 offhand, 41-44 sperret.
 */
public class InvseeContainer implements Container {
    private final Player target;

    public InvseeContainer(Player target) {
        this.target = target;
    }

    private Inventory inv() {
        return target.getInventory();
    }

    @Override
    public int getContainerSize() {
        return 45;
    }

    @Override
    public boolean isEmpty() {
        return inv().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 36) return inv().getItem(slot);
        if (slot < 40) return inv().armor.get(slot - 36);
        if (slot == 40) return inv().offhand.get(0);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = stack.split(amount);
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 36) {
            inv().setItem(slot, stack);
        } else if (slot < 40) {
            inv().armor.set(slot - 36, stack);
        } else if (slot == 40) {
            inv().offhand.set(0, stack);
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        inv().setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return !target.isRemoved()
                && (player.canInteractWithEntity(target, 128.0) || player.hasPermissions(2));
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot <= 40;
    }

    @Override
    public void clearContent() {}
}

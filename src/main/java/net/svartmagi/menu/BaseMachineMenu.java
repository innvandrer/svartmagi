package net.svartmagi.menu;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Felles: spillerinventar-slots + standard shift-klikk. */
public abstract class BaseMachineMenu extends AbstractContainerMenu {
    protected final int machineSlots;

    /** Settes av server-konstruktoren; null paa klienten er ok. */
    @Nullable
    protected BlockEntity blockEntity;

    protected BaseMachineMenu(MenuType<?> type, int containerId, int machineSlots) {
        super(type, containerId);
        this.machineSlots = machineSlots;
    }

    protected void addPlayerInventory(Inventory playerInventory, int yOffset) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, yOffset + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < machineSlots) {
            if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, machineSlots, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        // Lukker menyen naar blokken fjernes eller spilleren gaar for langt
        // unna, saa items ikke havner i en forlatt handler.
        return blockEntity == null || Container.stillValidBlockEntity(blockEntity, player);
    }
}

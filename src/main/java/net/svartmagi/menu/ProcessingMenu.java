package net.svartmagi.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.tech.ProcessingBlockEntity;

/** Meny for elektrisk ovn, knuser og infuser (1 input + 1 output). */
public class ProcessingMenu extends BaseMachineMenu {
    private final ContainerData data;

    public ProcessingMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(4));
    }

    public ProcessingMenu(int containerId, Inventory playerInventory, ProcessingBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.PROSESSERING.get(), containerId, 2);
        this.data = data;
        ItemStackHandler handler = blockEntity != null ? blockEntity.getInventory() : new ItemStackHandler(2);
        addSlot(new SlotItemHandler(handler, ProcessingBlockEntity.SLOT_INPUT, 56, 35));
        addSlot(new SlotItemHandler(handler, ProcessingBlockEntity.SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                return false;
            }
        });
        addPlayerInventory(playerInventory, 84);
        addDataSlots(data);
    }

    private static ProcessingBlockEntity clientBlockEntity(Inventory inv, BlockPos pos) {
        return inv.player.level().getBlockEntity(pos) instanceof ProcessingBlockEntity be ? be : null;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getTotalTime() {
        return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getCapacity() {
        return data.get(3);
    }
}

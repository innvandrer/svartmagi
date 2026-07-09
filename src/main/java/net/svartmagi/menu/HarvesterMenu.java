package net.svartmagi.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.tech.HarvesterBlockEntity;

/** 3x3-buffer (dispenser-layout) + energivisning. */
public class HarvesterMenu extends BaseMachineMenu {
    private final ContainerData data;

    public HarvesterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(4));
    }

    public HarvesterMenu(int containerId, Inventory playerInventory, HarvesterBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.INNHOSTER.get(), containerId, 9);
        this.data = data;
        this.blockEntity = blockEntity;
        ItemStackHandler handler = blockEntity != null ? blockEntity.getInventory() : new ItemStackHandler(9);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(handler, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerInventory(playerInventory, 84);
        addDataSlots(data);
    }

    private static HarvesterBlockEntity clientBlockEntity(Inventory inv, BlockPos pos) {
        return inv.player.level().getBlockEntity(pos) instanceof HarvesterBlockEntity be ? be : null;
    }

    public int getEnergy() {
        return (data.get(1) << 16) | (data.get(0) & 0xFFFF);
    }

    public int getCapacity() {
        return (data.get(3) << 16) | (data.get(2) & 0xFFFF);
    }
}

package net.svartmagi.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.tech.GeneratorBlockEntity;

public class GeneratorMenu extends BaseMachineMenu {
    private final ContainerData data;

    public GeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(4));
    }

    public GeneratorMenu(int containerId, Inventory playerInventory, GeneratorBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.KULLGENERATOR.get(), containerId, 1);
        this.data = data;
        ItemStackHandler handler = blockEntity != null ? blockEntity.getInventory() : new ItemStackHandler(1);
        addSlot(new SlotItemHandler(handler, GeneratorBlockEntity.SLOT_FUEL, 80, 40));
        addPlayerInventory(playerInventory, 84);
        addDataSlots(data);
    }

    private static GeneratorBlockEntity clientBlockEntity(Inventory inv, BlockPos pos) {
        return inv.player.level().getBlockEntity(pos) instanceof GeneratorBlockEntity be ? be : null;
    }

    public int getBurnTime() {
        return data.get(0);
    }

    public int getBurnDuration() {
        return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getCapacity() {
        return data.get(3);
    }
}

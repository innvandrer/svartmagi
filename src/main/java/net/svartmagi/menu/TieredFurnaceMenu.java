package net.svartmagi.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.tech.TieredFurnaceBlockEntity;

/** Ovnsmeny med vanilla-layout: input, brensel, output. */
public class TieredFurnaceMenu extends BaseMachineMenu {
    private final ContainerData data;

    public TieredFurnaceMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(6));
    }

    public TieredFurnaceMenu(int containerId, Inventory playerInventory, TieredFurnaceBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.TIER_OVN.get(), containerId, 3);
        this.data = data;
        this.blockEntity = blockEntity;
        ItemStackHandler handler = blockEntity != null ? blockEntity.getInventory() : new ItemStackHandler(3);
        addSlot(new SlotItemHandler(handler, TieredFurnaceBlockEntity.SLOT_INPUT, 56, 17));
        addSlot(new SlotItemHandler(handler, TieredFurnaceBlockEntity.SLOT_FUEL, 56, 53));
        addSlot(new SlotItemHandler(handler, TieredFurnaceBlockEntity.SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                return false;
            }
        });
        addPlayerInventory(playerInventory, 84);
        addDataSlots(data);
    }

    private static TieredFurnaceBlockEntity clientBlockEntity(Inventory inv, BlockPos pos) {
        return inv.player.level().getBlockEntity(pos) instanceof TieredFurnaceBlockEntity be ? be : null;
    }

    public int getBurnTime() {
        return data.get(0);
    }

    public int getBurnDuration() {
        return data.get(1);
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getTotalTime() {
        return data.get(3);
    }

    public int getSpeedUpgrades() {
        return data.get(4);
    }

    public int getParallelUpgrades() {
        return data.get(5);
    }
}

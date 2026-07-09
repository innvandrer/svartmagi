package net.svartmagi.menu;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.storage.UpgradableChestBlockEntity;

/** Kistemeny med variabelt antall rader (3-6). */
public class UpgradableChestMenu extends BaseMachineMenu {
    private final int rows;
    private final IItemHandler handler;

    public static UpgradableChestMenu forClient(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof UpgradableChestBlockEntity chest) {
            return new UpgradableChestMenu(containerId, playerInventory, chest.getInventory(), chest.getTier().rows, chest);
        }
        return new UpgradableChestMenu(containerId, playerInventory, new ItemStackHandler(27), 3, null);
    }

    public UpgradableChestMenu(int containerId, Inventory playerInventory, IItemHandler handler, int rows,
                               @Nullable UpgradableChestBlockEntity blockEntity) {
        super(ModMenus.OPPGRADERBAR_KISTE.get(), containerId, rows * 9);
        this.rows = rows;
        this.handler = handler;
        this.blockEntity = blockEntity;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        addPlayerInventory(playerInventory, 18 + rows * 18 + 13);
    }

    public int getRows() {
        return rows;
    }

    /** Brukes ved kisteoppgradering for aa finne aapne menyer mot gammel handler. */
    public boolean isFor(IItemHandler handler) {
        return this.handler == handler;
    }

    /** Kiste-BE for oppgraderingsvisning i GUI (null i fallback paa klienten). */
    @Nullable
    public UpgradableChestBlockEntity chest() {
        return blockEntity instanceof UpgradableChestBlockEntity chest ? chest : null;
    }
}

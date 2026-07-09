package net.svartmagi.storage;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.item.UpgradeItem;
import net.svartmagi.menu.UpgradableChestMenu;
import net.svartmagi.registry.ModBlockEntities;

public class UpgradableChestBlockEntity extends BlockEntity implements MenuProvider {
    public enum Tier implements net.minecraft.util.StringRepresentable {
        BASIS(3),
        JERN(4),
        GULL(5),
        DIAMANT(6);

        public final int rows;

        Tier(int rows) {
            this.rows = rows;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    private Tier tier = Tier.BASIS;
    private int stackUpgrades;
    private ItemStackHandler inventory = createHandler(Tier.BASIS.rows * 9);

    public UpgradableChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OPPGRADERBAR_KISTE.get(), pos, state);
    }

    private ItemStackHandler createHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64 << stackUpgrades;
            }
        };
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public Tier getTier() {
        return tier;
    }

    public int getStackLimit() {
        return 64 << stackUpgrades;
    }

    public boolean tryInstallUpgrade(UpgradeItem.Kind kind, Player player) {
        Tier target = switch (kind) {
            case CHEST_JERN -> Tier.JERN;
            case CHEST_GULL -> Tier.GULL;
            case CHEST_DIAMANT -> Tier.DIAMANT;
            case CHEST_STACK -> null;
            default -> {
                player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_not_supported"), true);
                yield null;
            }
        };

        if (kind == UpgradeItem.Kind.CHEST_STACK) {
            if (stackUpgrades >= SvartmagiConfig.CHEST_MAX_STACK_UPGRADES.get()) {
                player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_full"), true);
                return false;
            }
            stackUpgrades++;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_installed"), true);
            return true;
        }

        if (target == null) return false;
        if (target.ordinal() != tier.ordinal() + 1) {
            player.displayClientMessage(Component.translatable("message.svartmagi.chest_wrong_tier"), true);
            return false;
        }

        // Lukk aapne menyer mot den gamle handleren foer byttet, ellers
        // skriver andre spillere items inn i en forlatt handler (item-tap).
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (net.minecraft.server.level.ServerPlayer viewer : serverLevel.players()) {
                if (viewer.containerMenu instanceof UpgradableChestMenu menu && menu.isFor(inventory)) {
                    viewer.closeContainer();
                }
            }
        }

        ItemStackHandler bigger = createHandler(target.rows * 9);
        for (int i = 0; i < inventory.getSlots(); i++) {
            bigger.setStackInSlot(i, inventory.getStackInSlot(i));
        }
        inventory = bigger;
        tier = target;
        setChanged();
        invalidateCapabilities();
        // Tieren er en blockstate-property (styrer teksturen), saa
        // level.setBlock synker den til klienten - egen blokk-klasse
        // beholdes saa BlockEntityen ikke fjernes/gjenskapes.
        level.setBlock(worldPosition, getBlockState().setValue(UpgradableChestBlock.TIER, tier), 3);
        player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_installed"), true);
        return true;
    }

    public void dropContents() {
        if (level == null) return;
        for (int i = 0; i < inventory.getSlots(); i++) {
            // Store stacks kan overstige vanlig max; del dem opp ved drop
            ItemStack stack = inventory.getStackInSlot(i);
            while (!stack.isEmpty()) {
                ItemStack part = stack.split(Math.min(stack.getCount(), stack.getMaxStackSize()));
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), part);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Tier", tier.name());
        tag.putInt("StackUpgrades", stackUpgrades);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            tier = Tier.valueOf(tag.getString("Tier"));
        } catch (IllegalArgumentException e) {
            tier = Tier.BASIS;
        }
        stackUpgrades = tag.getInt("StackUpgrades");
        inventory = createHandler(tier.rows * 9);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }

    /**
     * Retter opp blockstate-tieren mot lagret NBT-tier ved chunk-last.
     * Trengs for kister oppgradert foer TIER-blockstate-propertyen fantes.
     */
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(UpgradableChestBlock.TIER) && state.getValue(UpgradableChestBlock.TIER) != tier) {
                level.setBlock(worldPosition, state.setValue(UpgradableChestBlock.TIER, tier), 3);
            }
        }
    }

    // Synk tier/stack-oppgraderinger til klienten kun ved endring/chunk-last
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Tier", tier.name());
        tag.putInt("StackUpgrades", stackUpgrades);
        return tag;
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this,
                (be, registries) -> ((UpgradableChestBlockEntity) be).getUpdateTag(registries));
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new UpgradableChestMenu(containerId, playerInventory, inventory, tier.rows, this);
    }
}

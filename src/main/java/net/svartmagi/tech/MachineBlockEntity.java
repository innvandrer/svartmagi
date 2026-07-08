package net.svartmagi.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.item.UpgradeItem;

/**
 * Felles base for maskiner. Nettverkssync skjer kun via blockstate-endringer
 * (LIT) og ContainerData mens menyen er aapen - aldri per tick til alle.
 */
public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {
    protected final ItemStackHandler inventory;
    protected final EnergyBuffer energy;

    protected int speedUpgrades;
    protected int parallelUpgrades;

    private BlockCapabilityCache<IEnergyStorage, Direction>[] energyNeighbors;
    private BlockCapabilityCache<IItemHandler, Direction>[] itemNeighbors;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots) {
        super(type, pos, state);
        this.inventory = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                MachineBlockEntity.this.onInventoryChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
                return MachineBlockEntity.this.isItemValid(slot, stack);
            }
        };
        this.energy = new EnergyBuffer(SvartmagiConfig.MACHINE_CAPACITY.get(), 4000, 4000, this::setChanged);
    }

    public abstract void serverTick();

    protected void onInventoryChanged(int slot) {}

    protected boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
        return true;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public EnergyBuffer getEnergy() {
        return energy;
    }

    public int getSpeedUpgrades() {
        return speedUpgrades;
    }

    public int getParallelUpgrades() {
        return parallelUpgrades;
    }

    /** Hvilke oppgraderinger maskinen godtar. */
    protected int maxUpgrades(UpgradeItem.Kind kind) {
        return 0;
    }

    public boolean tryInstallUpgrade(UpgradeItem.Kind kind, Player player) {
        int max = maxUpgrades(kind);
        if (max <= 0) {
            player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_not_supported"), true);
            return false;
        }
        int current = switch (kind) {
            case SPEED -> speedUpgrades;
            case PARALLEL -> parallelUpgrades;
            default -> Integer.MAX_VALUE;
        };
        if (current >= max) {
            player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_full"), true);
            return false;
        }
        switch (kind) {
            case SPEED -> speedUpgrades++;
            case PARALLEL -> parallelUpgrades++;
            default -> { return false; }
        }
        setChanged();
        player.displayClientMessage(Component.translatable("message.svartmagi.upgrade_installed"), true);
        return true;
    }

    protected void setLit(boolean lit) {
        BlockState state = getBlockState();
        if (state.hasProperty(MachineBlock.LIT) && state.getValue(MachineBlock.LIT) != lit) {
            level.setBlock(worldPosition, state.setValue(MachineBlock.LIT, lit), 3);
        }
    }

    @SuppressWarnings("unchecked")
    protected IEnergyStorage energyNeighbor(Direction dir) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        if (energyNeighbors == null) {
            energyNeighbors = new BlockCapabilityCache[6];
        }
        int i = dir.get3DDataValue();
        if (energyNeighbors[i] == null) {
            energyNeighbors[i] = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, serverLevel,
                    worldPosition.relative(dir), dir.getOpposite());
        }
        return energyNeighbors[i].getCapability();
    }

    @SuppressWarnings("unchecked")
    protected IItemHandler itemNeighbor(Direction dir) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        if (itemNeighbors == null) {
            itemNeighbors = new BlockCapabilityCache[6];
        }
        int i = dir.get3DDataValue();
        if (itemNeighbors[i] == null) {
            itemNeighbors[i] = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel,
                    worldPosition.relative(dir), dir.getOpposite());
        }
        return itemNeighbors[i].getCapability();
    }

    public void dropContents() {
        if (level == null) return;
        for (int i = 0; i < inventory.getSlots(); i++) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    inventory.getStackInSlot(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("SpeedUpgrades", speedUpgrades);
        tag.putInt("ParallelUpgrades", parallelUpgrades);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        energy.setEnergy(tag.getInt("Energy"));
        speedUpgrades = tag.getInt("SpeedUpgrades");
        parallelUpgrades = tag.getInt("ParallelUpgrades");
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }
}

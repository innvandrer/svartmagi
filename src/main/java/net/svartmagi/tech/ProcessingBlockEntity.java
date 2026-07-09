package net.svartmagi.tech;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.item.UpgradeItem;
import net.svartmagi.menu.ProcessingMenu;

/**
 * Base for maskiner med 1 input + 1 output som kjoerer en oppskrift over tid.
 * Oppskrifts-oppslag caches og re-evalueres kun naar input endres
 * (ikke per tick).
 */
public abstract class ProcessingBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    protected int progress;
    protected int totalTime;

    /** Cached resultat av oppskrifts-oppslaget for gjeldende input. */
    @Nullable
    private CachedRecipe cachedRecipe;
    private boolean recipeDirty = true;

    protected record CachedRecipe(ItemStack result, int processingTime, int maxParallel) {}

    // Energi/kapasitet splittes i lav/hoy 16-bit halvdel: vanilla-synken
    // sender ContainerData-verdier som short, saa alt over 32767 kuttes.
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> totalTime;
                case 2 -> energy.getEnergyStored() & 0xFFFF;
                case 3 -> (energy.getEnergyStored() >> 16) & 0xFFFF;
                case 4 -> energy.getCapacity() & 0xFFFF;
                case 5 -> (energy.getCapacity() >> 16) & 0xFFFF;
                case 6 -> speedUpgrades;
                case 7 -> parallelUpgrades;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> totalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    protected ProcessingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 2);
    }

    /** Slaa opp oppskrift for gjeldende input, eller empty om ingen. */
    protected abstract Optional<CachedRecipe> lookupRecipe(ItemStack input);

    @Override
    protected void onInventoryChanged(int slot) {
        if (slot == SLOT_INPUT) {
            recipeDirty = true;
        }
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return slot == SLOT_INPUT;
    }

    @Override
    protected int maxUpgrades(UpgradeItem.Kind kind) {
        return switch (kind) {
            case SPEED -> 3;
            case PARALLEL -> 2;
            default -> 0;
        };
    }

    private CachedRecipe currentRecipe() {
        if (recipeDirty) {
            recipeDirty = false;
            ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
            cachedRecipe = input.isEmpty() ? null : lookupRecipe(input).orElse(null);
        }
        return cachedRecipe;
    }

    @Override
    public void serverTick() {
        if (!SvartmagiConfig.TECH_ENABLED.get()) {
            setLit(false);
            return;
        }
        CachedRecipe recipe = currentRecipe();
        boolean canWork = recipe != null && canFitOutput(recipe.result())
                && energy.getEnergyStored() >= SvartmagiConfig.MACHINE_FE_PER_TICK.get();

        if (canWork) {
            totalTime = Math.max(1, recipe.processingTime() / (1 + speedUpgrades));
            energy.consume(SvartmagiConfig.MACHINE_FE_PER_TICK.get() * (1 + speedUpgrades));
            progress++;
            if (progress >= totalTime) {
                progress = 0;
                finishRecipe(recipe);
            }
            setLit(true);
        } else {
            if (progress != 0) {
                progress = 0;
                setChanged();
            }
            setLit(false);
        }
    }

    private boolean canFitOutput(ItemStack result) {
        ItemStack out = inventory.getStackInSlot(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(out, result)
                && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private void finishRecipe(CachedRecipe recipe) {
        // Parallellprosessering: behandle opptil N inputs per syklus.
        int parallel = Math.min(recipe.maxParallel(), 1 + parallelUpgrades * 2);
        ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
        int operations = Math.min(parallel, input.getCount());

        ItemStack out = inventory.getStackInSlot(SLOT_OUTPUT);
        int space = out.isEmpty() ? recipe.result().getMaxStackSize()
                : out.getMaxStackSize() - out.getCount();
        operations = Math.min(operations, space / Math.max(1, recipe.result().getCount()));
        if (operations <= 0) return;

        input.shrink(operations);
        // shrink() gaar utenom onContentsChanged, saa cachen maa markeres
        // manuelt - ellers fortsetter maskinen aa trekke FE med tom input.
        recipeDirty = true;
        if (out.isEmpty()) {
            ItemStack produced = recipe.result().copy();
            produced.setCount(recipe.result().getCount() * operations);
            inventory.setStackInSlot(SLOT_OUTPUT, produced);
        } else {
            out.grow(recipe.result().getCount() * operations);
            inventory.setStackInSlot(SLOT_OUTPUT, out);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Progress", progress);
        tag.putInt("TotalTime", totalTime);
    }

    @Override
    protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("Progress");
        totalTime = tag.getInt("TotalTime");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ProcessingMenu(containerId, playerInventory, this, data);
    }
}

package net.svartmagi.tech;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.menu.GeneratorMenu;
import net.svartmagi.registry.ModBlockEntities;

/** Kullgenerator: brenner ovnsbrensel og produserer FE, dytter til naboer. */
public class GeneratorBlockEntity extends MachineBlockEntity {
    public static final int SLOT_FUEL = 0;

    private int burnTime;
    private int burnDuration;

    // Energi/kapasitet splittes i lav/hoy 16-bit halvdel: vanilla-synken
    // sender ContainerData-verdier som short, saa alt over 32767 kuttes.
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnDuration;
                case 2 -> energy.getEnergyStored() & 0xFFFF;
                case 3 -> (energy.getEnergyStored() >> 16) & 0xFFFF;
                case 4 -> energy.getCapacity() & 0xFFFF;
                case 5 -> (energy.getCapacity() >> 16) & 0xFFFF;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnDuration = value;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KULLGENERATOR.get(), pos, state, 1);
    }

    @Override
    public void serverTick() {
        if (!SvartmagiConfig.TECH_ENABLED.get()) {
            setLit(false);
            return;
        }
        boolean wasBurning = burnTime > 0;

        if (burnTime > 0) {
            burnTime--;
            energy.generate(SvartmagiConfig.GENERATOR_FE_PER_TICK.get());
        }

        if (burnTime <= 0 && energy.getEnergyStored() < energy.getCapacity()) {
            ItemStack fuel = inventory.getStackInSlot(SLOT_FUEL);
            int time = fuel.getBurnTime(RecipeType.SMELTING);
            if (time > 0) {
                burnTime = time;
                burnDuration = time;
                ItemStack remainder = fuel.getCraftingRemainingItem();
                fuel.shrink(1);
                if (fuel.isEmpty() && !remainder.isEmpty()) {
                    inventory.setStackInSlot(SLOT_FUEL, remainder);
                }
                setChanged();
            }
        }

        pushEnergy();

        if (wasBurning != burnTime > 0) {
            setLit(burnTime > 0);
            setChanged();
        }
    }

    private void pushEnergy() {
        int available = energy.getEnergyStored();
        if (available <= 0) return;
        for (Direction dir : Direction.values()) {
            IEnergyStorage neighbor = energyNeighbor(dir);
            if (neighbor == null || !neighbor.canReceive()) continue;
            int accepted = neighbor.receiveEnergy(Math.min(available, 4000), false);
            if (accepted > 0) {
                energy.consume(accepted);
                available -= accepted;
                if (available <= 0) return;
            }
        }
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING) > 0;
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
    }

    @Override
    protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        burnTime = tag.getInt("BurnTime");
        burnDuration = tag.getInt("BurnDuration");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GeneratorMenu(containerId, playerInventory, this, data);
    }
}

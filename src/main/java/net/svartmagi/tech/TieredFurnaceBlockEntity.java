package net.svartmagi.tech;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.menu.TieredFurnaceMenu;
import net.svartmagi.registry.ModBlockEntities;

/** Brenselsdrevet ovn med fartsmultiplikator fra blokk-tieren. */
public class TieredFurnaceBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;

    private int burnTime;
    private int burnDuration;
    private int progress;
    private int totalTime = 200;

    @Nullable
    private ItemStack cachedResult;
    private int cachedCookTime = 200;
    private boolean recipeDirty = true;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnDuration;
                case 2 -> progress;
                case 3 -> totalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnDuration = value;
                case 2 -> progress = value;
                case 3 -> totalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TieredFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIER_OVN.get(), pos, state, 3);
    }

    private float speedMultiplier() {
        return getBlockState().getBlock() instanceof TieredFurnaceBlock furnace
                ? furnace.tier().speedMultiplier : 1.0f;
    }

    @Override
    protected int maxUpgrades(net.svartmagi.item.UpgradeItem.Kind kind) {
        return switch (kind) {
            case SPEED -> 3;
            case PARALLEL -> 2;
            default -> 0;
        };
    }

    @Override
    protected void onInventoryChanged(int slot) {
        if (slot == SLOT_INPUT) recipeDirty = true;
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        if (slot == SLOT_OUTPUT) return false;
        if (slot == SLOT_FUEL) return stack.getBurnTime(RecipeType.SMELTING) > 0;
        return true;
    }

    private Optional<ItemStack> currentResult() {
        if (recipeDirty) {
            recipeDirty = false;
            ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
            if (input.isEmpty()) {
                cachedResult = null;
            } else {
                var recipe = level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level);
                cachedResult = recipe.map(h -> h.value().getResultItem(level.registryAccess()).copy()).orElse(null);
                cachedCookTime = recipe.map(h -> h.value().getCookingTime()).orElse(200);
            }
        }
        return Optional.ofNullable(cachedResult);
    }

    @Override
    public void serverTick() {
        if (!net.svartmagi.SvartmagiConfig.TECH_ENABLED.get()) {
            setLit(false);
            return;
        }
        boolean wasBurning = burnTime > 0;
        if (burnTime > 0) burnTime--;

        Optional<ItemStack> result = currentResult();
        boolean canSmelt = result.isPresent() && canFit(result.get());

        if (canSmelt && burnTime <= 0) {
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
            }
        }

        if (canSmelt && burnTime > 0) {
            totalTime = Math.max(1, Mth.floor(cachedCookTime / (speedMultiplier() * (1 + speedUpgrades))));
            progress++;
            if (progress >= totalTime) {
                progress = 0;
                finishSmelt(result.get());
                setChanged();
            }
        } else if (progress != 0) {
            progress = 0;
            setChanged();
        }

        if (wasBurning != burnTime > 0) {
            setLit(burnTime > 0);
            setChanged();
        }
    }

    /** Parallellprosessering: smelt opptil N inputs per syklus (parallelloppgraderinger). */
    private void finishSmelt(ItemStack result) {
        int parallel = 1 + parallelUpgrades * 2;
        ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
        int operations = Math.min(parallel, input.getCount());

        ItemStack out = inventory.getStackInSlot(SLOT_OUTPUT);
        int space = out.isEmpty() ? result.getMaxStackSize() : out.getMaxStackSize() - out.getCount();
        operations = Math.min(operations, space / Math.max(1, result.getCount()));
        if (operations <= 0) return;

        input.shrink(operations);
        if (out.isEmpty()) {
            ItemStack produced = result.copy();
            produced.setCount(result.getCount() * operations);
            inventory.setStackInSlot(SLOT_OUTPUT, produced);
        } else {
            out.grow(result.getCount() * operations);
            inventory.setStackInSlot(SLOT_OUTPUT, out);
        }
        recipeDirty = true;
    }

    private boolean canFit(ItemStack result) {
        ItemStack out = inventory.getStackInSlot(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(out, result)
                && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        burnTime = tag.getInt("BurnTime");
        burnDuration = tag.getInt("BurnDuration");
        progress = tag.getInt("Progress");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TieredFurnaceMenu(containerId, playerInventory, this, data);
    }
}

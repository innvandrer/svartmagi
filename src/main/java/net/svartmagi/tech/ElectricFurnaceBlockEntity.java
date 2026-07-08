package net.svartmagi.tech;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.registry.ModBlockEntities;

/** Elektrisk ovn: bruker vanilla smelte-oppskrifter, drives av FE. */
public class ElectricFurnaceBlockEntity extends ProcessingBlockEntity {
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEKTRISK_OVN.get(), pos, state);
    }

    @Override
    protected Optional<CachedRecipe> lookupRecipe(ItemStack input) {
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level)
                .map(holder -> new CachedRecipe(
                        holder.value().getResultItem(level.registryAccess()).copy(),
                        holder.value().getCookingTime(),
                        8));
    }
}

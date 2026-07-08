package net.svartmagi.tech;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.registry.ModBlockEntities;
import net.svartmagi.registry.ModRecipes;

/** Knuser: malm -> 2x knust malm (data-drevne oppskrifter). */
public class CrusherBlockEntity extends ProcessingBlockEntity {
    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KNUSER.get(), pos, state);
    }

    @Override
    protected Optional<CachedRecipe> lookupRecipe(ItemStack input) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.CRUSHING.get(), new SingleRecipeInput(input), level)
                .map(holder -> new CachedRecipe(
                        holder.value().result().copy(),
                        holder.value().processingTime(),
                        4));
    }
}

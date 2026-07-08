package net.svartmagi.tech;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.registry.ModBlockEntities;
import net.svartmagi.registry.ModRecipes;

/**
 * Skyggeinfuser: lader items med energi (broen mellom tech og magi).
 * F.eks. skyggeskaar -> ladet skyggeskaar.
 */
public class InfuserBlockEntity extends ProcessingBlockEntity {
    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SKYGGEINFUSER.get(), pos, state);
    }

    @Override
    protected Optional<CachedRecipe> lookupRecipe(ItemStack input) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.INFUSING.get(), new SingleRecipeInput(input), level)
                .map(holder -> new CachedRecipe(
                        holder.value().result().copy(),
                        holder.value().processingTime(),
                        2));
    }
}

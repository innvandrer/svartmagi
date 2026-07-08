package net.svartmagi.integration.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.svartmagi.Svartmagi;
import net.svartmagi.recipe.RitualRecipe;
import net.svartmagi.recipe.SimpleProcessingRecipe;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModRecipes;

@JeiPlugin
public class SvartmagiJeiPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<SimpleProcessingRecipe.Crushing>> CRUSHING =
            RecipeType.createFromVanilla(ModRecipes.CRUSHING.get());
    public static final RecipeType<RecipeHolder<SimpleProcessingRecipe.Infusing>> INFUSING =
            RecipeType.createFromVanilla(ModRecipes.INFUSING.get());
    public static final RecipeType<RecipeHolder<RitualRecipe>> RITUAL =
            RecipeType.createFromVanilla(ModRecipes.RITUAL.get());

    @Override
    public ResourceLocation getPluginUid() {
        return Svartmagi.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ProcessingRecipeCategory<>(guiHelper, CRUSHING, "jei.svartmagi.crushing",
                        new ItemStack(ModBlocks.KNUSER.get())),
                new ProcessingRecipeCategory<>(guiHelper, INFUSING, "jei.svartmagi.infusing",
                        new ItemStack(ModBlocks.SKYGGEINFUSER.get())),
                new RitualRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<RecipeHolder<SimpleProcessingRecipe.Crushing>> crushing =
                recipeManager.getAllRecipesFor(ModRecipes.CRUSHING.get());
        List<RecipeHolder<SimpleProcessingRecipe.Infusing>> infusing =
                recipeManager.getAllRecipesFor(ModRecipes.INFUSING.get());
        List<RecipeHolder<RitualRecipe>> rituals =
                recipeManager.getAllRecipesFor(ModRecipes.RITUAL.get());
        registration.addRecipes(CRUSHING, crushing);
        registration.addRecipes(INFUSING, infusing);
        registration.addRecipes(RITUAL, rituals);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.KNUSER.get()), CRUSHING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SKYGGEINFUSER.get()), INFUSING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RITUALALTER.get()), RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PIDESTALL.get()), RITUAL);
    }
}

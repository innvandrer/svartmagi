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
    // Lazy: plugin-klassen lastes under mod-konstruksjon, foer registrene er
    // ferdige - saa registry-oppslag maa vente til JEI faktisk kaller oss.
    private static RecipeType<RecipeHolder<SimpleProcessingRecipe.Crushing>> crushingType;
    private static RecipeType<RecipeHolder<SimpleProcessingRecipe.Infusing>> infusingType;
    private static RecipeType<RecipeHolder<RitualRecipe>> ritualType;

    public static synchronized RecipeType<RecipeHolder<SimpleProcessingRecipe.Crushing>> crushing() {
        if (crushingType == null) crushingType = RecipeType.createFromVanilla(ModRecipes.CRUSHING.get());
        return crushingType;
    }

    public static synchronized RecipeType<RecipeHolder<SimpleProcessingRecipe.Infusing>> infusing() {
        if (infusingType == null) infusingType = RecipeType.createFromVanilla(ModRecipes.INFUSING.get());
        return infusingType;
    }

    public static synchronized RecipeType<RecipeHolder<RitualRecipe>> ritual() {
        if (ritualType == null) ritualType = RecipeType.createFromVanilla(ModRecipes.RITUAL.get());
        return ritualType;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return Svartmagi.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ProcessingRecipeCategory<>(guiHelper, crushing(), "jei.svartmagi.crushing",
                        new ItemStack(ModBlocks.KNUSER.get())),
                new ProcessingRecipeCategory<>(guiHelper, infusing(), "jei.svartmagi.infusing",
                        new ItemStack(ModBlocks.SKYGGEINFUSER.get())),
                new RitualRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<RecipeHolder<SimpleProcessingRecipe.Crushing>> crushingRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.CRUSHING.get());
        List<RecipeHolder<SimpleProcessingRecipe.Infusing>> infusingRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.INFUSING.get());
        List<RecipeHolder<RitualRecipe>> ritualRecipes =
                recipeManager.getAllRecipesFor(ModRecipes.RITUAL.get());
        registration.addRecipes(crushing(), crushingRecipes);
        registration.addRecipes(infusing(), infusingRecipes);
        registration.addRecipes(ritual(), ritualRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.KNUSER.get()), crushing());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SKYGGEINFUSER.get()), infusing());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RITUALALTER.get()), ritual());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PIDESTALL.get()), ritual());
    }
}

package net.svartmagi.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.svartmagi.recipe.SimpleProcessingRecipe;

/** JEI-kategori for knusing og infusering (1 input -> 1 output). */
public class ProcessingRecipeCategory<T extends SimpleProcessingRecipe>
        implements IRecipeCategory<RecipeHolder<T>> {
    private final RecipeType<RecipeHolder<T>> recipeType;
    private final Component title;
    private final IDrawable icon;

    public ProcessingRecipeCategory(IGuiHelper guiHelper, RecipeType<RecipeHolder<T>> recipeType,
                                    String titleKey, ItemStack iconStack) {
        this.recipeType = recipeType;
        this.title = Component.translatable(titleKey);
        this.icon = guiHelper.createDrawableItemLike(iconStack.getItem());
    }

    @Override
    public RecipeType<RecipeHolder<T>> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> holder, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 8).addIngredients(holder.value().ingredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 8).addItemStack(holder.value().result());
    }

    @Override
    public void draw(RecipeHolder<T> recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView slotsView,
                     net.minecraft.client.gui.GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font,
                (recipe.value().processingTime() / 20) + "s", 50, 12, 0xFF808080, false);
    }
}

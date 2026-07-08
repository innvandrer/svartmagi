package net.svartmagi.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.svartmagi.recipe.RitualRecipe;
import net.svartmagi.registry.ModBlocks;

/** JEI-kategori for ritualer: senter-item + pidestall-ingredienser. */
public class RitualRecipeCategory implements IRecipeCategory<RecipeHolder<RitualRecipe>> {
    private final IDrawable icon;

    public RitualRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(ModBlocks.RITUALALTER.get());
    }

    @Override
    public RecipeType<RecipeHolder<RitualRecipe>> getRecipeType() {
        return SvartmagiJeiPlugin.RITUAL;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.svartmagi.ritual");
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 64;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<RitualRecipe> holder, IFocusGroup focuses) {
        RitualRecipe recipe = holder.value();
        // Senter-item paa alteret
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 24).addIngredients(recipe.center());
        // Pidestall-ingredienser
        for (int i = 0; i < recipe.pedestalItems().size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 36 + (i % 4) * 18, 8 + (i / 4) * 18)
                    .addIngredients(recipe.pedestalItems().get(i));
        }
        if (!recipe.result().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 134, 24).addItemStack(recipe.result());
        }
    }

    @Override
    public void draw(RecipeHolder<RitualRecipe> holder, mezz.jei.api.gui.ingredient.IRecipeSlotsView slotsView,
                     net.minecraft.client.gui.GuiGraphics graphics, double mouseX, double mouseY) {
        String key = "jei.svartmagi.ritual.outcome." + holder.value().outcome().getSerializedName();
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font,
                Component.translatable(key), 8, 52, 0xFF606060, false);
    }
}

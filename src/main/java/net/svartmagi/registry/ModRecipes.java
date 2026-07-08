package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.svartmagi.Svartmagi;
import net.svartmagi.recipe.RitualRecipe;
import net.svartmagi.recipe.SimpleProcessingRecipe;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Svartmagi.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Svartmagi.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SimpleProcessingRecipe.Crushing>> CRUSHING =
            RECIPE_TYPES.register("crushing", () -> RecipeType.<SimpleProcessingRecipe.Crushing>simple(Svartmagi.id("crushing")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SimpleProcessingRecipe.Infusing>> INFUSING =
            RECIPE_TYPES.register("infusing", () -> RecipeType.<SimpleProcessingRecipe.Infusing>simple(Svartmagi.id("infusing")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<RitualRecipe>> RITUAL =
            RECIPE_TYPES.register("ritual", () -> RecipeType.<RitualRecipe>simple(Svartmagi.id("ritual")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleProcessingRecipe.Crushing>> CRUSHING_SERIALIZER =
            RECIPE_SERIALIZERS.register("crushing",
                    () -> new SimpleProcessingRecipe.Serializer<>(SimpleProcessingRecipe.Crushing::new, 200));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleProcessingRecipe.Infusing>> INFUSING_SERIALIZER =
            RECIPE_SERIALIZERS.register("infusing",
                    () -> new SimpleProcessingRecipe.Serializer<>(SimpleProcessingRecipe.Infusing::new, 300));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RitualRecipe>> RITUAL_SERIALIZER =
            RECIPE_SERIALIZERS.register("ritual", RitualRecipe.Serializer::new);

    private ModRecipes() {}

    // Praktisk alias for vanlig input-type
    public static SingleRecipeInput input(net.minecraft.world.item.ItemStack stack) {
        return new SingleRecipeInput(stack);
    }
}

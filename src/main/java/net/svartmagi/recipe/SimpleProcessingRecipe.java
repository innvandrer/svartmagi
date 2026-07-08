package net.svartmagi.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/** Data-drevet 1-input -> 1-output-oppskrift med tid (knusing, infusering). */
public abstract class SimpleProcessingRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final int processingTime;

    protected SimpleProcessingRecipe(Ingredient ingredient, ItemStack result, int processingTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.processingTime = processingTime;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack result() {
        return result;
    }

    public int processingTime() {
        return processingTime;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    public static class Serializer<T extends SimpleProcessingRecipe> implements RecipeSerializer<T> {
        public interface Factory<T> {
            T create(Ingredient ingredient, ItemStack result, int processingTime);
        }

        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Factory<T> factory, int defaultTime) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("time", defaultTime).forGetter(r -> r.processingTime)
            ).apply(instance, factory::create));
            this.streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                    ItemStack.STREAM_CODEC, r -> r.result,
                    ByteBufCodecs.VAR_INT, r -> r.processingTime,
                    factory::create);
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }
    }

    public static final class Crushing extends SimpleProcessingRecipe {
        public Crushing(Ingredient ingredient, ItemStack result, int processingTime) {
            super(ingredient, result, processingTime);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return net.svartmagi.registry.ModRecipes.CRUSHING_SERIALIZER.get();
        }

        @Override
        public RecipeType<?> getType() {
            return net.svartmagi.registry.ModRecipes.CRUSHING.get();
        }
    }

    public static final class Infusing extends SimpleProcessingRecipe {
        public Infusing(Ingredient ingredient, ItemStack result, int processingTime) {
            super(ingredient, result, processingTime);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return net.svartmagi.registry.ModRecipes.INFUSING_SERIALIZER.get();
        }

        @Override
        public RecipeType<?> getType() {
            return net.svartmagi.registry.ModRecipes.INFUSING.get();
        }
    }
}

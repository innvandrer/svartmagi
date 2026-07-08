package net.svartmagi.recipe;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.svartmagi.registry.ModRecipes;

/**
 * Data-drevet ritual: senter-item paa alteret + items paa pidestallene rundt.
 * Resultatet er enten et item, en portal eller en boss-summon.
 */
public class RitualRecipe implements Recipe<RitualRecipe.AltarInput> {
    public enum Outcome implements StringRepresentable {
        ITEM("item"),
        PORTAL("portal"),
        SUMMON_BOSS("summon_boss");

        public static final Codec<Outcome> CODEC = StringRepresentable.fromEnum(Outcome::values);
        private final String name;

        Outcome(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    /** Input: item paa alteret + items paa pidestallene (uordnet). */
    public record AltarInput(ItemStack center, List<ItemStack> pedestals) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? center : pedestals.get(index - 1);
        }

        @Override
        public int size() {
            return 1 + pedestals.size();
        }
    }

    private final Ingredient center;
    private final NonNullList<Ingredient> pedestalItems;
    private final ItemStack result;
    private final Outcome outcome;

    public RitualRecipe(Ingredient center, List<Ingredient> pedestalItems, ItemStack result, Outcome outcome) {
        this.center = center;
        this.pedestalItems = NonNullList.copyOf(pedestalItems);
        this.result = result;
        this.outcome = outcome;
    }

    public Ingredient center() {
        return center;
    }

    public List<Ingredient> pedestalItems() {
        return pedestalItems;
    }

    public ItemStack result() {
        return result;
    }

    public Outcome outcome() {
        return outcome;
    }

    @Override
    public boolean matches(AltarInput input, Level level) {
        if (!center.test(input.center())) return false;
        List<ItemStack> available = input.pedestals().stream().filter(s -> !s.isEmpty()).toList();
        if (available.size() != pedestalItems.size()) return false;

        // Uordnet matching: hver ingrediens maa finne et unikt pidestall-item.
        boolean[] used = new boolean[available.size()];
        for (Ingredient ingredient : pedestalItems) {
            boolean found = false;
            for (int i = 0; i < available.size(); i++) {
                if (!used[i] && ingredient.test(available.get(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(AltarInput input, HolderLookup.Provider registries) {
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL.get();
    }

    public static final class Serializer implements RecipeSerializer<RitualRecipe> {
        public static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("center").forGetter(r -> r.center),
                Ingredient.CODEC_NONEMPTY.listOf(1, 8).fieldOf("pedestals").forGetter(r -> r.pedestalItems),
                ItemStack.STRICT_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(r -> r.result),
                Outcome.CODEC.optionalFieldOf("outcome", Outcome.ITEM).forGetter(r -> r.outcome)
        ).apply(instance, RitualRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.center,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> (List<Ingredient>) r.pedestalItems,
                ItemStack.OPTIONAL_STREAM_CODEC, r -> r.result,
                ByteBufCodecs.fromCodec(Outcome.CODEC), r -> r.outcome,
                RitualRecipe::new);

        @Override
        public MapCodec<RitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

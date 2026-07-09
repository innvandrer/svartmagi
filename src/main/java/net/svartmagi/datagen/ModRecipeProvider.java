package net.svartmagi.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.svartmagi.Svartmagi;
import net.svartmagi.recipe.RitualRecipe;
import net.svartmagi.recipe.SimpleProcessingRecipe;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModItems;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // --- Tech: maskiner ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MASKINKJERNE.get())
                .pattern("III").pattern("IRI").pattern("III")
                .define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.KULLGENERATOR.get())
                .pattern("CCC").pattern("CMC").pattern("CFC")
                .define('C', Items.COBBLESTONE).define('M', ModItems.MASKINKJERNE.get()).define('F', Items.FURNACE)
                .unlockedBy("has_maskinkjerne", has(ModItems.MASKINKJERNE.get())).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ELEKTRISK_OVN.get())
                .pattern("III").pattern("RMR").pattern("IFI")
                .define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .define('M', ModItems.MASKINKJERNE.get()).define('F', Items.FURNACE)
                .unlockedBy("has_maskinkjerne", has(ModItems.MASKINKJERNE.get())).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.KNUSER.get())
                .pattern("FFF").pattern("IMI").pattern("III")
                .define('F', Items.FLINT).define('I', Items.IRON_INGOT).define('M', ModItems.MASKINKJERNE.get())
                .unlockedBy("has_maskinkjerne", has(ModItems.MASKINKJERNE.get())).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SKYGGEINFUSER.get())
                .pattern("OAO").pattern("AMA").pattern("OOO")
                .define('O', Items.OBSIDIAN).define('A', Items.AMETHYST_SHARD).define('M', ModItems.MASKINKJERNE.get())
                .unlockedBy("has_maskinkjerne", has(ModItems.MASKINKJERNE.get())).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.INNHOSTER.get())
                .pattern("AMH").pattern("III").pattern("IRI")
                .define('A', Items.IRON_AXE).define('H', Items.IRON_HOE)
                .define('M', ModItems.MASKINKJERNE.get()).define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_maskinkjerne", has(ModItems.MASKINKJERNE.get())).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.UTTREKKER.get(), 2)
                .pattern("IRI").pattern("RHR").pattern("IRI")
                .define('I', Items.IRON_INGOT).define('R', Items.REDSTONE).define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.KRAFTKABEL.get(), 6)
                .pattern(" C ").pattern("CRC").pattern(" C ")
                .define('C', Items.COPPER_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHUNKLASTER.get())
                .pattern("EPE").pattern("RIR").pattern("EPE")
                .define('E', Items.ENDER_PEARL).define('P', Items.IRON_BLOCK)
                .define('R', Items.REDSTONE_BLOCK).define('I', ModItems.MASKINKJERNE.get())
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL)).save(output);

        // Tier-ovner
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.KOBBEROVN.get())
                .pattern("CCC").pattern("CFC").pattern("CCC")
                .define('C', Items.COPPER_INGOT).define('F', Items.FURNACE)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.JERNOVN.get())
                .pattern("III").pattern("IKI").pattern("III")
                .define('I', Items.IRON_INGOT).define('K', ModBlocks.KOBBEROVN.get())
                .unlockedBy("has_kobberovn", has(ModBlocks.KOBBEROVN.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DIAMANTOVN.get())
                .pattern("DDD").pattern("DJD").pattern("DDD")
                .define('D', Items.DIAMOND).define('J', ModBlocks.JERNOVN.get())
                .unlockedBy("has_jernovn", has(ModBlocks.JERNOVN.get())).save(output);

        // --- Lagring ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.OPPGRADERBAR_KISTE.get())
                .pattern("PPP").pattern("PCP").pattern("PPP")
                .define('P', net.minecraft.tags.ItemTags.PLANKS).define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.KISTEOPPGRADERING_JERN.get())
                .pattern("III").pattern("ICI").pattern("III")
                .define('I', Items.IRON_INGOT).define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.KISTEOPPGRADERING_GULL.get())
                .pattern("GGG").pattern("GUG").pattern("GGG")
                .define('G', Items.GOLD_INGOT).define('U', ModItems.KISTEOPPGRADERING_JERN.get())
                .unlockedBy("has_jern_upgrade", has(ModItems.KISTEOPPGRADERING_JERN.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.KISTEOPPGRADERING_DIAMANT.get())
                .pattern("DDD").pattern("DUD").pattern("DDD")
                .define('D', Items.DIAMOND).define('U', ModItems.KISTEOPPGRADERING_GULL.get())
                .unlockedBy("has_gull_upgrade", has(ModItems.KISTEOPPGRADERING_GULL.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STABELOPPGRADERING.get())
                .pattern("IOI").pattern("OEO").pattern("IOI")
                .define('I', Items.IRON_INGOT).define('O', Items.OBSIDIAN).define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL)).save(output);

        // Maskin-oppgraderinger
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FARTSOPPGRADERING.get())
                .pattern("GRG").pattern("RSR").pattern("GRG")
                .define('G', Items.GOLD_INGOT).define('R', Items.REDSTONE).define('S', Items.SUGAR)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PARALLELLOPPGRADERING.get())
                .pattern("GQG").pattern("QRQ").pattern("GQG")
                .define('G', Items.GOLD_INGOT).define('Q', Items.QUARTZ).define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_quartz", has(Items.QUARTZ)).save(output);

        // --- Magi ---
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RITUALALTER.get())
                .pattern("ASA").pattern("SOS").pattern("OOO")
                .define('A', Items.AMETHYST_SHARD).define('S', Items.POLISHED_BLACKSTONE).define('O', Items.OBSIDIAN)
                .unlockedBy("has_obsidian", has(Items.OBSIDIAN)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.PIDESTALL.get(), 4)
                .pattern(" S ").pattern(" S ").pattern("SSS")
                .define('S', Items.POLISHED_BLACKSTONE)
                .unlockedBy("has_blackstone", has(Items.POLISHED_BLACKSTONE)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SKYGGESKAAR.get(), 2)
                .requires(Items.AMETHYST_SHARD).requires(Items.OBSIDIAN).requires(Items.OBSIDIAN).requires(Items.COAL)
                .unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD)).save(output);

        // Skyggestaal (toppnivaa, kombinerer alle pilarene)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SKYGGESTAAL_BARRE.get(), 4)
                .pattern("SNS").pattern("NKN").pattern("SNS")
                .define('S', ModItems.SKYGGESTOV.get()).define('N', Items.NETHERITE_INGOT)
                .define('K', ModItems.SKYGGEKJERNE.get())
                .unlockedBy("has_skyggekjerne", has(ModItems.SKYGGEKJERNE.get())).save(output);

        toolAndArmorRecipes(output);

        // Smelting av knuste malmer (dobling via knuser)
        oreSmelting(output, ModItems.KNUST_JERN.get(), Items.IRON_INGOT);
        oreSmelting(output, ModItems.KNUST_GULL.get(), Items.GOLD_INGOT);
        oreSmelting(output, ModItems.KNUST_KOBBER.get(), Items.COPPER_INGOT);

        // --- Custom: knusing (data-drevet) ---
        crushing(output, "knust_jern_fra_raajern", Ingredient.of(Items.RAW_IRON),
                new ItemStack(ModItems.KNUST_JERN.get(), 2), 200);
        crushing(output, "knust_gull_fra_raagull", Ingredient.of(Items.RAW_GOLD),
                new ItemStack(ModItems.KNUST_GULL.get(), 2), 200);
        crushing(output, "knust_kobber_fra_raakobber", Ingredient.of(Items.RAW_COPPER),
                new ItemStack(ModItems.KNUST_KOBBER.get(), 2), 200);
        crushing(output, "grus_fra_brostein", Ingredient.of(Items.COBBLESTONE),
                new ItemStack(Items.GRAVEL), 100);
        crushing(output, "sand_fra_grus", Ingredient.of(Items.GRAVEL),
                new ItemStack(Items.SAND), 100);
        crushing(output, "skyggestov_fra_skyggemalm", Ingredient.of(ModBlocks.SKYGGEMALM.get()),
                new ItemStack(ModItems.SKYGGESTOV.get(), 3), 240);

        // --- Custom: infusering (tech -> magi-broen) ---
        infusing(output, "ladet_skyggeskaar", Ingredient.of(ModItems.SKYGGESKAAR.get()),
                new ItemStack(ModItems.LADET_SKYGGESKAAR.get()), 400);

        // --- Custom: ritualer ---
        ritual(output, "aapne_skyggeportal",
                Ingredient.of(ModItems.LADET_SKYGGESKAAR.get()),
                List.of(Ingredient.of(Items.ENDER_PEARL), Ingredient.of(Items.ENDER_PEARL),
                        Ingredient.of(Items.OBSIDIAN), Ingredient.of(Items.OBSIDIAN)),
                ItemStack.EMPTY, RitualRecipe.Outcome.PORTAL);
        ritual(output, "paakall_skyggevokteren",
                Ingredient.of(ModItems.LADET_SKYGGESKAAR.get()),
                List.of(Ingredient.of(ModItems.SKYGGESTOV.get()), Ingredient.of(ModItems.SKYGGESTOV.get()),
                        Ingredient.of(ModItems.SKYGGESTOV.get()), Ingredient.of(Items.DIAMOND)),
                ItemStack.EMPTY, RitualRecipe.Outcome.SUMMON_BOSS);
        ritual(output, "gjenkallingsamulett",
                Ingredient.of(Items.ENDER_PEARL),
                List.of(Ingredient.of(Items.GOLD_INGOT), Ingredient.of(Items.GOLD_INGOT),
                        Ingredient.of(ModItems.LADET_SKYGGESKAAR.get())),
                new ItemStack(ModItems.GJENKALLINGSAMULETT.get()), RitualRecipe.Outcome.ITEM);
        ritual(output, "skyggeskaar_ritual",
                Ingredient.of(Items.AMETHYST_SHARD),
                List.of(Ingredient.of(ModItems.SKYGGESTOV.get()), Ingredient.of(ModItems.SKYGGESTOV.get())),
                new ItemStack(ModItems.SKYGGESKAAR.get(), 4), RitualRecipe.Outcome.ITEM);
    }

    private void toolAndArmorRecipes(RecipeOutput output) {
        ItemLike barre = ModItems.SKYGGESTAAL_BARRE.get();
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SKYGGESTAAL_SVERD.get())
                .pattern("B").pattern("B").pattern("S")
                .define('B', barre).define('S', Items.STICK)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SKYGGESTAAL_HAKKE.get())
                .pattern("BBB").pattern(" S ").pattern(" S ")
                .define('B', barre).define('S', Items.STICK)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SKYGGESTAAL_OKS.get())
                .pattern("BB").pattern("BS").pattern(" S")
                .define('B', barre).define('S', Items.STICK)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SKYGGESTAAL_SPADE.get())
                .pattern("B").pattern("S").pattern("S")
                .define('B', barre).define('S', Items.STICK)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SKYGGESTAAL_GREIP.get())
                .pattern("BB").pattern(" S").pattern(" S")
                .define('B', barre).define('S', Items.STICK)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SKYGGESTAAL_HJELM.get())
                .pattern("BBB").pattern("B B")
                .define('B', barre)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SKYGGESTAAL_BRYNJE.get())
                .pattern("B B").pattern("BBB").pattern("BBB")
                .define('B', barre)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SKYGGESTAAL_BUKSER.get())
                .pattern("BBB").pattern("B B").pattern("B B")
                .define('B', barre)
                .unlockedBy("has_barre", has(barre)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SKYGGESTAAL_STOVLER.get())
                .pattern("B B").pattern("B B")
                .define('B', barre)
                .unlockedBy("has_barre", has(barre)).save(output);
    }

    private void oreSmelting(RecipeOutput output, ItemLike input, ItemLike result) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, 0.7f, 150)
                .unlockedBy("has_input", has(input))
                .save(output, Svartmagi.id("smelting/" + net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(result.asItem()).getPath() + "_fra_knust"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, result, 0.7f, 75)
                .unlockedBy("has_input", has(input))
                .save(output, Svartmagi.id("blasting/" + net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(result.asItem()).getPath() + "_fra_knust"));
    }

    private void crushing(RecipeOutput output, String name, Ingredient input, ItemStack result, int time) {
        output.accept(Svartmagi.id("crushing/" + name),
                new SimpleProcessingRecipe.Crushing(input, result, time), null);
    }

    private void infusing(RecipeOutput output, String name, Ingredient input, ItemStack result, int time) {
        output.accept(Svartmagi.id("infusing/" + name),
                new SimpleProcessingRecipe.Infusing(input, result, time), null);
    }

    private void ritual(RecipeOutput output, String name, Ingredient center, List<Ingredient> pedestals,
                        ItemStack result, RitualRecipe.Outcome outcome) {
        output.accept(Svartmagi.id("ritual/" + name),
                new RitualRecipe(center, pedestals, result, outcome), null);
    }
}

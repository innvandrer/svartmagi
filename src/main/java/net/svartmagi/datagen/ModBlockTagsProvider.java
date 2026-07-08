package net.svartmagi.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.svartmagi.Svartmagi;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModTags;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                ExistingFileHelper existingFileHelper) {
        super(output, registries, Svartmagi.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.KULLGENERATOR.get(), ModBlocks.ELEKTRISK_OVN.get(), ModBlocks.KNUSER.get(),
                ModBlocks.SKYGGEINFUSER.get(), ModBlocks.INNHOSTER.get(), ModBlocks.UTTREKKER.get(),
                ModBlocks.KOBBEROVN.get(), ModBlocks.JERNOVN.get(), ModBlocks.DIAMANTOVN.get(),
                ModBlocks.RITUALALTER.get(), ModBlocks.PIDESTALL.get(),
                ModBlocks.SKYGGEMALM.get(), ModBlocks.SKYGGESTEIN.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.OPPGRADERBAR_KISTE.get());

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                ModBlocks.KULLGENERATOR.get(), ModBlocks.ELEKTRISK_OVN.get(), ModBlocks.KNUSER.get(),
                ModBlocks.SKYGGEINFUSER.get(), ModBlocks.INNHOSTER.get(), ModBlocks.UTTREKKER.get(),
                ModBlocks.KOBBEROVN.get(), ModBlocks.JERNOVN.get(), ModBlocks.DIAMANTOVN.get(),
                ModBlocks.PIDESTALL.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                ModBlocks.RITUALALTER.get(), ModBlocks.SKYGGEMALM.get(), ModBlocks.SKYGGESTEIN.get());

        // Veinmineable: malmer og stokker (data-drevet, kan endres i datapack)
        tag(ModTags.VEINMINEABLE)
                .addTag(BlockTags.COAL_ORES)
                .addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.COPPER_ORES)
                .addTag(BlockTags.GOLD_ORES)
                .addTag(BlockTags.REDSTONE_ORES)
                .addTag(BlockTags.LAPIS_ORES)
                .addTag(BlockTags.DIAMOND_ORES)
                .addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.LOGS)
                .add(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE,
                        net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE,
                        net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS,
                        ModBlocks.SKYGGEMALM.get());
    }
}

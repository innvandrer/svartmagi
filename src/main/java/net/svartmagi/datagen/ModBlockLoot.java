package net.svartmagi.datagen;

import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModItems;

public class ModBlockLoot extends BlockLootSubProvider {
    public ModBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.KULLGENERATOR.get());
        dropSelf(ModBlocks.ELEKTRISK_OVN.get());
        dropSelf(ModBlocks.KNUSER.get());
        dropSelf(ModBlocks.SKYGGEINFUSER.get());
        dropSelf(ModBlocks.INNHOSTER.get());
        dropSelf(ModBlocks.UTTREKKER.get());
        dropSelf(ModBlocks.KRAFTKABEL.get());
        dropSelf(ModBlocks.CHUNKLASTER.get());
        dropSelf(ModBlocks.KOBBEROVN.get());
        dropSelf(ModBlocks.JERNOVN.get());
        dropSelf(ModBlocks.DIAMANTOVN.get());
        dropSelf(ModBlocks.OPPGRADERBAR_KISTE.get());
        dropSelf(ModBlocks.RITUALALTER.get());
        dropSelf(ModBlocks.PIDESTALL.get());
        dropSelf(ModBlocks.SKYGGESTEIN.get());

        // Skyggemalm dropper stov (paavirkes av Fortune)
        add(ModBlocks.SKYGGEMALM.get(), block -> createOreDrop(block, ModItems.SKYGGESTOV.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(e -> (Block) e.get())
                .filter(b -> b != ModBlocks.SKYGGEPORTAL.get())
                .toList();
    }
}

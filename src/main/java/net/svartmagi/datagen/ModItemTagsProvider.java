package net.svartmagi.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.svartmagi.Svartmagi;
import net.svartmagi.registry.ModItems;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                               CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, registries, blockTags, Svartmagi.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.SWORDS).add(ModItems.SKYGGESTAAL_SVERD.get());
        tag(ItemTags.PICKAXES).add(ModItems.SKYGGESTAAL_HAKKE.get());
        tag(ItemTags.AXES).add(ModItems.SKYGGESTAAL_OKS.get());
        tag(ItemTags.SHOVELS).add(ModItems.SKYGGESTAAL_SPADE.get());
        tag(ItemTags.HOES).add(ModItems.SKYGGESTAAL_GREIP.get());
        tag(ItemTags.HEAD_ARMOR).add(ModItems.SKYGGESTAAL_HJELM.get());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.SKYGGESTAAL_BRYNJE.get());
        tag(ItemTags.LEG_ARMOR).add(ModItems.SKYGGESTAAL_BUKSER.get());
        tag(ItemTags.FOOT_ARMOR).add(ModItems.SKYGGESTAAL_STOVLER.get());

        tag(TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots")))
                .add(ModItems.SKYGGESTAAL_BARRE.get());
        tag(TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "dusts")))
                .add(ModItems.SKYGGESTOV.get());
    }
}

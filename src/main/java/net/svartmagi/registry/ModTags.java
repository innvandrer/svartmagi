package net.svartmagi.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.svartmagi.Svartmagi;

public final class ModTags {
    /** Blokker som kan veinmines (malmer, stokker m.m.) - data-drevet. */
    public static final TagKey<Block> VEINMINEABLE =
            TagKey.create(Registries.BLOCK, Svartmagi.id("veinmineable"));

    private ModTags() {}
}

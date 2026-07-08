package net.svartmagi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.svartmagi.registry.ModBlockEntities;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModEntities;
import net.svartmagi.registry.ModItems;
import net.svartmagi.registry.ModMaterials;
import net.svartmagi.registry.ModMenus;
import net.svartmagi.registry.ModRecipes;
import net.svartmagi.registry.ModTabs;

@Mod(Svartmagi.MODID)
public final class Svartmagi {
    public static final String MODID = "svartmagi";

    public Svartmagi(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModMaterials.ARMOR_MATERIALS.register(modBus);
        ModTabs.TABS.register(modBus);

        container.registerConfig(ModConfig.Type.SERVER, SvartmagiConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

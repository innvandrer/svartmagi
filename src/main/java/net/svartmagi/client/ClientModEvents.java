package net.svartmagi.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.svartmagi.Svartmagi;
import net.svartmagi.client.screen.GeneratorScreen;
import net.svartmagi.client.screen.HarvesterScreen;
import net.svartmagi.client.screen.ProcessingScreen;
import net.svartmagi.client.screen.TieredFurnaceScreen;
import net.svartmagi.client.screen.UpgradableChestScreen;
import net.svartmagi.registry.ModEntities;
import net.svartmagi.registry.ModMenus;

@EventBusSubscriber(modid = Svartmagi.MODID, value = Dist.CLIENT)
public final class ClientModEvents {
    public static final KeyMapping VEINMINE_KEY = new KeyMapping(
            "key.svartmagi.veinmine", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.svartmagi");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(VEINMINE_KEY);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.KULLGENERATOR.get(), GeneratorScreen::new);
        event.register(ModMenus.PROSESSERING.get(), ProcessingScreen::new);
        event.register(ModMenus.TIER_OVN.get(), TieredFurnaceScreen::new);
        event.register(ModMenus.INNHOSTER.get(), HarvesterScreen::new);
        event.register(ModMenus.OPPGRADERBAR_KISTE.get(), UpgradableChestScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SKYGGEVOKTER.get(), SkyggevokterRenderer::new);
    }
}

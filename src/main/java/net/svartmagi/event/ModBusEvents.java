package net.svartmagi.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.svartmagi.Svartmagi;
import net.svartmagi.entity.SkyggevokterEntity;
import net.svartmagi.registry.ModBlockEntities;
import net.svartmagi.registry.ModEntities;
import net.svartmagi.veinmine.VeinMineHandler;
import net.svartmagi.veinmine.VeinMinePayload;

@EventBusSubscriber(modid = Svartmagi.MODID)
public final class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Item handlers for automasjon (hoppere, roer, andre mods)
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.KULLGENERATOR.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ELEKTRISK_OVN.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.KNUSER.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SKYGGEINFUSER.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.INNHOSTER.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TIER_OVN.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.OPPGRADERBAR_KISTE.get(),
                (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.UTTREKKER.get(),
                (be, side) -> be.getBuffer());

        // Energi
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.KULLGENERATOR.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.ELEKTRISK_OVN.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.KNUSER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SKYGGEINFUSER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.INNHOSTER.get(),
                (be, side) -> be.getEnergy());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SKYGGEVOKTER.get(), SkyggevokterEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(VeinMinePayload.TYPE, VeinMinePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        VeinMineHandler.setActive(context.player().getUUID(), payload.active())));
    }
}

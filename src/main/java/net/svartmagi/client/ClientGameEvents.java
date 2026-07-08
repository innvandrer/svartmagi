package net.svartmagi.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.svartmagi.Svartmagi;
import net.svartmagi.veinmine.VeinMinePayload;

@EventBusSubscriber(modid = Svartmagi.MODID, value = Dist.CLIENT)
public final class ClientGameEvents {
    private static boolean veinmineHeld;

    /** Pakke sendes KUN naar tastetilstanden endres, ikke hver tick. */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            veinmineHeld = false;
            return;
        }
        boolean held = ClientModEvents.VEINMINE_KEY.isDown();
        if (held != veinmineHeld) {
            veinmineHeld = held;
            PacketDistributor.sendToServer(new VeinMinePayload(held));
        }
    }
}

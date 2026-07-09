package net.svartmagi.chunkload;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.svartmagi.Svartmagi;

@EventBusSubscriber(modid = Svartmagi.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ChunkLoaderModEvents {
    @SubscribeEvent
    public static void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(ChunkLoaderTickets.CONTROLLER);
    }

    private ChunkLoaderModEvents() {}
}

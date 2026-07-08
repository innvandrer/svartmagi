package net.svartmagi.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.svartmagi.Svartmagi;
import net.svartmagi.command.ModCommands;
import net.svartmagi.command.TpaManager;
import net.svartmagi.veinmine.VeinMineHandler;

@EventBusSubscriber(modid = Svartmagi.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        VeinMineHandler.onBlockBroken(event);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ModCommands.onPlayerLeave(event.getEntity().getUUID());
        VeinMineHandler.onPlayerLeave(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Husk doedsposisjon for /back
        if (event.getEntity() instanceof ServerPlayer player) {
            ModCommands.rememberBackPosition(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        TpaManager.clearAll();
    }
}

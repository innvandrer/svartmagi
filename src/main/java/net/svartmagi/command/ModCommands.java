package net.svartmagi.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.svartmagi.SvartmagiConfig;

/**
 * QoL-kommandoer: /tpa, /tpahere, /tpaccept, /tpdeny, /rtp, /spawn,
 * /setspawn, /sethome, /home, /delhome, /homes, /back, /invsee.
 */
public final class ModCommands {
    /** Siste posisjon foer teleport/doed, for /back (i minnet). */
    private static final Map<UUID, GlobalPos> BACK_POSITIONS = new HashMap<>();
    private static final Map<UUID, Long> RTP_COOLDOWNS = new HashMap<>();

    public static void rememberBackPosition(ServerPlayer player) {
        BACK_POSITIONS.put(player.getUUID(), GlobalPos.of(player.level().dimension(), player.blockPosition()));
    }

    public static void onPlayerLeave(UUID player) {
        TpaManager.clear(player);
        // RTP_COOLDOWNS beholdes bevisst: aa fjerne den her lot spillere
        // omgaa cooldownen ved aa relogge.
    }

    /**
     * Kommandoene registreres alltid, men sperres ved kjoering hvis
     * commandsEnabled er av - da kan admin togglet dem uten restart.
     */
    private static com.mojang.brigadier.Command<CommandSourceStack> guarded(
            com.mojang.brigadier.Command<CommandSourceStack> command) {
        return ctx -> {
            if (!SvartmagiConfig.SPEC.isLoaded() || !SvartmagiConfig.COMMANDS_ENABLED.get()) {
                ctx.getSource().sendFailure(Component.translatable("command.svartmagi.disabled"));
                return 0;
            }
            return command.run(ctx);
        };
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(guarded(ctx -> tpa(ctx.getSource().getPlayerOrException(),
                                EntityArgument.getPlayer(ctx, "target"), false)))));

        dispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(guarded(ctx -> tpa(ctx.getSource().getPlayerOrException(),
                                EntityArgument.getPlayer(ctx, "target"), true)))));

        dispatcher.register(Commands.literal("tpaccept")
                .executes(guarded(ctx -> tpaccept(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("tpdeny")
                .executes(guarded(ctx -> tpdeny(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("rtp")
                .executes(guarded(ctx -> rtp(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("spawn")
                .executes(guarded(ctx -> spawn(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("setspawn")
                .requires(source -> source.hasPermission(2))
                .executes(guarded(ctx -> setspawn(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("sethome")
                .executes(guarded(ctx -> sethome(ctx.getSource().getPlayerOrException(), "home")))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(guarded(ctx -> sethome(ctx.getSource().getPlayerOrException(),
                                StringArgumentType.getString(ctx, "name"))))));

        dispatcher.register(Commands.literal("home")
                .executes(guarded(ctx -> home(ctx.getSource().getPlayerOrException(), "home")))
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ServerPlayer player = ctx.getSource().getPlayer();
                            if (player != null) {
                                HomesData.get(player.server).homesFor(player.getUUID())
                                        .keySet().forEach(builder::suggest);
                            }
                            return builder.buildFuture();
                        })
                        .executes(guarded(ctx -> home(ctx.getSource().getPlayerOrException(),
                                StringArgumentType.getString(ctx, "name"))))));

        dispatcher.register(Commands.literal("delhome")
                .executes(guarded(ctx -> delhome(ctx.getSource().getPlayerOrException(), "home")))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(guarded(ctx -> delhome(ctx.getSource().getPlayerOrException(),
                                StringArgumentType.getString(ctx, "name"))))));

        dispatcher.register(Commands.literal("homes")
                .executes(guarded(ctx -> homes(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("back")
                .executes(guarded(ctx -> back(ctx.getSource().getPlayerOrException()))));

        dispatcher.register(Commands.literal("invsee")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(guarded(ctx -> invsee(ctx.getSource().getPlayerOrException(),
                                EntityArgument.getPlayer(ctx, "target"))))));
    }

    private static int tpa(ServerPlayer from, ServerPlayer to, boolean here) {
        if (from == to) {
            from.displayClientMessage(Component.translatable("command.svartmagi.tpa_self"), false);
            return 0;
        }
        TpaManager.request(from.getUUID(), to.getUUID(), here);
        from.displayClientMessage(Component.translatable("command.svartmagi.tpa_sent", to.getDisplayName()), false);
        to.displayClientMessage(Component.translatable(
                here ? "command.svartmagi.tpahere_received" : "command.svartmagi.tpa_received",
                from.getDisplayName()), false);
        return 1;
    }

    private static int tpaccept(ServerPlayer player) {
        TpaManager.Request request = TpaManager.take(player.getUUID());
        if (request == null) {
            player.displayClientMessage(Component.translatable("command.svartmagi.tpa_none"), false);
            return 0;
        }
        ServerPlayer other = player.server.getPlayerList().getPlayer(request.from());
        if (other == null) {
            player.displayClientMessage(Component.translatable("command.svartmagi.tpa_offline"), false);
            return 0;
        }
        ServerPlayer moving = request.here() ? player : other;
        ServerPlayer destination = request.here() ? other : player;
        rememberBackPosition(moving);
        moving.teleportTo((ServerLevel) destination.level(), destination.getX(), destination.getY(),
                destination.getZ(), destination.getYRot(), destination.getXRot());
        player.displayClientMessage(Component.translatable("command.svartmagi.tpa_accepted"), false);
        other.displayClientMessage(Component.translatable("command.svartmagi.tpa_accepted"), false);
        return 1;
    }

    private static int tpdeny(ServerPlayer player) {
        TpaManager.Request request = TpaManager.take(player.getUUID());
        if (request == null) {
            player.displayClientMessage(Component.translatable("command.svartmagi.tpa_none"), false);
            return 0;
        }
        ServerPlayer other = player.server.getPlayerList().getPlayer(request.from());
        if (other != null) {
            other.displayClientMessage(Component.translatable("command.svartmagi.tpa_denied_sender",
                    player.getDisplayName()), false);
        }
        player.displayClientMessage(Component.translatable("command.svartmagi.tpa_denied"), false);
        return 1;
    }

    private static int rtp(ServerPlayer player) {
        long now = System.currentTimeMillis();
        long cooldownMs = SvartmagiConfig.RTP_COOLDOWN_SECONDS.get() * 1000L;
        Long last = RTP_COOLDOWNS.get(player.getUUID());
        if (last != null && now - last < cooldownMs) {
            long remaining = (cooldownMs - (now - last)) / 1000;
            player.displayClientMessage(Component.translatable("command.svartmagi.rtp_cooldown", remaining), false);
            return 0;
        }

        // Alltid i oververdenen, uansett hvilken dimensjon spilleren staar i.
        // Skyggeverden er End-lignende med svevende oyer i tomt rom - et
        // radius-soek der ville nesten alltid gaa tomt for forsoek.
        ServerLevel level = player.server.overworld();
        RandomSource random = level.random;
        int radius = SvartmagiConfig.RTP_RADIUS.get();
        BlockPos spawnPos = level.getSharedSpawnPos();

        for (int attempt = 0; attempt < 16; attempt++) {
            int x = spawnPos.getX() + random.nextInt(radius * 2) - radius;
            int z = spawnPos.getZ() + random.nextInt(radius * 2) - radius;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (y <= level.getMinBuildHeight() + 1) continue;
            BlockPos ground = new BlockPos(x, y - 1, z);
            var groundState = level.getBlockState(ground);
            if (groundState.liquid() || groundState.isAir()) continue;

            RTP_COOLDOWNS.put(player.getUUID(), now);
            rememberBackPosition(player);
            player.teleportTo(level, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
            player.displayClientMessage(Component.translatable("command.svartmagi.rtp_success", x, y, z), false);
            return 1;
        }
        player.displayClientMessage(Component.translatable("command.svartmagi.rtp_failed"), false);
        return 0;
    }

    private static int spawn(ServerPlayer player) {
        HomesData data = HomesData.get(player.server);
        GlobalPos target = data.getServerSpawn() != null ? data.getServerSpawn()
                : GlobalPos.of(Level.OVERWORLD, player.server.overworld().getSharedSpawnPos());
        rememberBackPosition(player);
        HomesData.teleportTo(player, target);
        player.displayClientMessage(Component.translatable("command.svartmagi.spawn_teleported"), false);
        return 1;
    }

    private static int setspawn(ServerPlayer player) {
        HomesData.get(player.server).setServerSpawn(
                GlobalPos.of(player.level().dimension(), player.blockPosition()));
        player.displayClientMessage(Component.translatable("command.svartmagi.setspawn_done"), false);
        return 1;
    }

    private static int sethome(ServerPlayer player, String name) {
        HomesData data = HomesData.get(player.server);
        Map<String, GlobalPos> playerHomes = data.homesFor(player.getUUID());
        if (!playerHomes.containsKey(name) && playerHomes.size() >= SvartmagiConfig.HOME_LIMIT.get()) {
            player.displayClientMessage(Component.translatable("command.svartmagi.home_limit",
                    SvartmagiConfig.HOME_LIMIT.get()), false);
            return 0;
        }
        data.setHome(player.getUUID(), name, GlobalPos.of(player.level().dimension(), player.blockPosition()));
        player.displayClientMessage(Component.translatable("command.svartmagi.sethome_done", name), false);
        return 1;
    }

    private static int home(ServerPlayer player, String name) {
        GlobalPos pos = HomesData.get(player.server).homesFor(player.getUUID()).get(name);
        if (pos == null) {
            player.displayClientMessage(Component.translatable("command.svartmagi.home_unknown", name), false);
            return 0;
        }
        rememberBackPosition(player);
        HomesData.teleportTo(player, pos);
        player.displayClientMessage(Component.translatable("command.svartmagi.home_teleported", name), false);
        return 1;
    }

    private static int delhome(ServerPlayer player, String name) {
        boolean removed = HomesData.get(player.server).deleteHome(player.getUUID(), name);
        player.displayClientMessage(Component.translatable(
                removed ? "command.svartmagi.delhome_done" : "command.svartmagi.home_unknown", name), false);
        return removed ? 1 : 0;
    }

    private static int homes(ServerPlayer player) {
        Map<String, GlobalPos> playerHomes = HomesData.get(player.server).homesFor(player.getUUID());
        if (playerHomes.isEmpty()) {
            player.displayClientMessage(Component.translatable("command.svartmagi.homes_none"), false);
        } else {
            player.displayClientMessage(Component.translatable("command.svartmagi.homes_list",
                    String.join(", ", playerHomes.keySet())), false);
        }
        return 1;
    }

    private static int back(ServerPlayer player) {
        GlobalPos pos = BACK_POSITIONS.get(player.getUUID());
        if (pos == null) {
            player.displayClientMessage(Component.translatable("command.svartmagi.back_none"), false);
            return 0;
        }
        rememberBackPosition(player);
        HomesData.teleportTo(player, pos);
        player.displayClientMessage(Component.translatable("command.svartmagi.back_teleported"), false);
        return 1;
    }

    private static int invsee(ServerPlayer viewer, ServerPlayer target) {
        InvseeContainer container = new InvseeContainer(target);
        viewer.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(MenuType.GENERIC_9x5, id, inv, container, 5),
                Component.translatable("command.svartmagi.invsee_title", target.getDisplayName())));
        return 1;
    }

    private ModCommands() {}
}

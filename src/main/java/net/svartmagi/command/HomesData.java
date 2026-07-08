package net.svartmagi.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/** Persistente homes + valgfri server-spawn, lagret i oververdenen. */
public class HomesData extends SavedData {
    private static final String NAME = "svartmagi_homes";

    private final Map<UUID, Map<String, GlobalPos>> homes = new HashMap<>();
    @Nullable
    private GlobalPos serverSpawn;

    public static HomesData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(new Factory<>(HomesData::new, HomesData::load), NAME);
    }

    public Map<String, GlobalPos> homesFor(UUID player) {
        return homes.computeIfAbsent(player, k -> new HashMap<>());
    }

    public void setHome(UUID player, String name, GlobalPos pos) {
        homesFor(player).put(name, pos);
        setDirty();
    }

    public boolean deleteHome(UUID player, String name) {
        boolean removed = homesFor(player).remove(name) != null;
        if (removed) setDirty();
        return removed;
    }

    @Nullable
    public GlobalPos getServerSpawn() {
        return serverSpawn;
    }

    public void setServerSpawn(GlobalPos pos) {
        this.serverSpawn = pos;
        setDirty();
    }

    /** Brukt av gjenkallingsamuletten: hjem "home", ellers foerste home, ellers spawn. */
    public static boolean teleportHomeOrSpawn(ServerPlayer player) {
        HomesData data = get(player.server);
        Map<String, GlobalPos> playerHomes = data.homesFor(player.getUUID());
        GlobalPos target = playerHomes.getOrDefault("home",
                playerHomes.values().stream().findFirst().orElse(null));
        if (target == null) {
            target = data.serverSpawn != null ? data.serverSpawn
                    : GlobalPos.of(Level.OVERWORLD, player.server.overworld().getSharedSpawnPos());
        }
        return teleportTo(player, target);
    }

    public static boolean teleportTo(ServerPlayer player, GlobalPos pos) {
        ServerLevel level = player.server.getLevel(pos.dimension());
        if (level == null) return false;
        BlockPos p = pos.pos();
        player.teleportTo(level, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, player.getYRot(), player.getXRot());
        return true;
    }

    private static HomesData load(CompoundTag tag, HolderLookup.Provider registries) {
        HomesData data = new HomesData();
        ListTag players = tag.getList("Players", Tag.TAG_COMPOUND);
        for (Tag t : players) {
            CompoundTag playerTag = (CompoundTag) t;
            UUID id = playerTag.getUUID("Id");
            Map<String, GlobalPos> map = new HashMap<>();
            ListTag homesList = playerTag.getList("Homes", Tag.TAG_COMPOUND);
            for (Tag h : homesList) {
                CompoundTag homeTag = (CompoundTag) h;
                map.put(homeTag.getString("Name"), readGlobalPos(homeTag));
            }
            data.homes.put(id, map);
        }
        if (tag.contains("ServerSpawn")) {
            data.serverSpawn = readGlobalPos(tag.getCompound("ServerSpawn"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag players = new ListTag();
        homes.forEach((id, map) -> {
            if (map.isEmpty()) return;
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("Id", id);
            ListTag homesList = new ListTag();
            map.forEach((name, pos) -> {
                CompoundTag homeTag = new CompoundTag();
                homeTag.putString("Name", name);
                writeGlobalPos(homeTag, pos);
                homesList.add(homeTag);
            });
            playerTag.put("Homes", homesList);
            players.add(playerTag);
        });
        tag.put("Players", players);
        if (serverSpawn != null) {
            CompoundTag spawnTag = new CompoundTag();
            writeGlobalPos(spawnTag, serverSpawn);
            tag.put("ServerSpawn", spawnTag);
        }
        return tag;
    }

    private static void writeGlobalPos(CompoundTag tag, GlobalPos pos) {
        tag.putString("Dim", pos.dimension().location().toString());
        tag.put("Pos", NbtUtils.writeBlockPos(pos.pos()));
    }

    private static GlobalPos readGlobalPos(CompoundTag tag) {
        ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION,
                ResourceLocation.parse(tag.getString("Dim")));
        BlockPos pos = NbtUtils.readBlockPos(tag, "Pos").orElse(BlockPos.ZERO);
        return GlobalPos.of(dim, pos);
    }
}

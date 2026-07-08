package net.svartmagi.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.svartmagi.Svartmagi;
import net.svartmagi.registry.ModBlocks;

/** Nokler og hjelpere for Skyggeverden-dimensjonen (data-drevet JSON). */
public final class SkyggeDimension {
    public static final ResourceKey<Level> SKYGGE_LEVEL =
            ResourceKey.create(Registries.DIMENSION, Svartmagi.id("skyggeverden"));

    private SkyggeDimension() {}

    /** Finn/lag en trygg landingsplass med portal i maal-dimensjonen. */
    public static BlockPos prepareArrival(ServerLevel target, BlockPos origin) {
        int x = origin.getX();
        int z = origin.getZ();
        int y = target.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        if (y <= target.getMinBuildHeight() || y >= target.getMaxBuildHeight() - 4) {
            y = Math.max(target.getMinBuildHeight() + 32, Math.min(72, target.getMaxBuildHeight() - 8));
            // Bygg en liten plattform i loese luften
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    target.setBlock(new BlockPos(x + dx, y - 1, z + dz),
                            ModBlocks.SKYGGESTEIN.get().defaultBlockState(), 3);
                }
            }
            for (int dy = 0; dy <= 2; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos clear = new BlockPos(x + dx, y + dy, z + dz);
                        if (!target.isEmptyBlock(clear)) {
                            target.removeBlock(clear, false);
                        }
                    }
                }
            }
        }
        BlockPos arrival = new BlockPos(x, y, z);
        BlockPos portalPos = arrival.above(2);
        if (target.isEmptyBlock(portalPos)) {
            target.setBlock(portalPos, ModBlocks.SKYGGEPORTAL.get().defaultBlockState(), 3);
        }
        return arrival;
    }
}

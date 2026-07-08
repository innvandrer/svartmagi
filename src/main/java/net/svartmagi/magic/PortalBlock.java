package net.svartmagi.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.magic.SkyggeDimension;

/**
 * Skyggeportal: teleporterer spillere mellom oververdenen og Skyggeverden.
 * Helt event-drevet via entityInside (ingen egen ticking).
 */
public class PortalBlock extends Block {
    public PortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) return;
        if (!SvartmagiConfig.MAGIC_ENABLED.get()) return;
        if (player.isOnPortalCooldown()) {
            player.setPortalCooldown();
            return;
        }

        ServerLevel target = level.dimension() == SkyggeDimension.SKYGGE_LEVEL
                ? level.getServer().overworld()
                : level.getServer().getLevel(SkyggeDimension.SKYGGE_LEVEL);
        if (target == null) return;

        BlockPos arrival = SkyggeDimension.prepareArrival(target, pos);
        player.setPortalCooldown();
        player.changeDimension(new DimensionTransition(target,
                new Vec3(arrival.getX() + 0.5, arrival.getY(), arrival.getZ() + 0.5),
                Vec3.ZERO, player.getYRot(), player.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND));
    }
}

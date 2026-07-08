package net.svartmagi.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.svartmagi.command.HomesData;

/** Gjenkallingsamulett: teleporterer deg hjem (foerste home, ellers spawn). */
public class RecallAmuletItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 60;

    public RecallAmuletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel)) {
            return InteractionResultHolder.consume(stack);
        }
        boolean teleported = HomesData.teleportHomeOrSpawn(serverPlayer);
        if (teleported) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            serverPlayer.displayClientMessage(Component.translatable("message.svartmagi.recalled"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.svartmagi.gjenkallingsamulett"));
    }
}

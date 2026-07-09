package net.svartmagi.storage;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.item.UpgradeItem;

/**
 * Oppgraderbar kiste: stoerrelse oppgraderes med jern/gull/diamant-
 * oppgraderinger, stack-stoerrelse med stabeloppgraderinger.
 * TIER er en blockstate-property saa hver tier faar sin egen tekstur
 * (jf. Sophisticated Storage-stilen: samme kropp, ulike hjoernebeslag).
 */
public class UpgradableChestBlock extends Block implements EntityBlock {
    public static final EnumProperty<UpgradableChestBlockEntity.Tier> TIER =
            EnumProperty.create("tier", UpgradableChestBlockEntity.Tier.class);

    public UpgradableChestBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TIER, UpgradableChestBlockEntity.Tier.BASIS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UpgradableChestBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof UpgradeItem upgrade
                && level.getBlockEntity(pos) instanceof UpgradableChestBlockEntity chest) {
            if (!level.isClientSide && SvartmagiConfig.STORAGE_ENABLED.get()
                    && chest.tryInstallUpgrade(upgrade.kind(), player)) {
                stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof UpgradableChestBlockEntity chest) {
            player.openMenu(chest, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof UpgradableChestBlockEntity chest) {
            chest.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

package net.svartmagi.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.entity.SkyggevokterEntity;
import net.svartmagi.recipe.RitualRecipe;
import net.svartmagi.registry.ModBlockEntities;
import net.svartmagi.registry.ModBlocks;
import net.svartmagi.registry.ModEntities;
import net.svartmagi.registry.ModRecipes;

/**
 * Selve ritual-logikken. Helt event-drevet: ingenting kjoerer per tick,
 * alt skjer naar en spiller aktiverer alteret.
 */
public class AltarBlockEntity extends BlockEntity {
    /** Pidestaller soekes i en 5x5-ring rundt alteret (samme Y). */
    private static final int PEDESTAL_RANGE = 2;

    private ItemStack centerItem = ItemStack.EMPTY;

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUALALTER.get(), pos, state);
    }

    public ItemStack getCenterItem() {
        return centerItem;
    }

    public void setCenterItem(ItemStack stack) {
        this.centerItem = stack;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void attemptRitual(Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!SvartmagiConfig.MAGIC_ENABLED.get()) {
            player.displayClientMessage(Component.translatable("message.svartmagi.magic_disabled"), true);
            return;
        }
        if (centerItem.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.svartmagi.altar_empty"), true);
            return;
        }

        List<PedestalBlockEntity> pedestals = findPedestals(serverLevel);
        List<ItemStack> pedestalItems = pedestals.stream().map(PedestalBlockEntity::getItem).toList();
        RitualRecipe.AltarInput input = new RitualRecipe.AltarInput(centerItem, pedestalItems);

        Optional<RitualRecipe> match = serverLevel.getRecipeManager()
                .getRecipeFor(ModRecipes.RITUAL.get(), input, serverLevel)
                .map(holder -> holder.value());

        if (match.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.svartmagi.ritual_no_match"), true);
            serverLevel.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7f, 0.6f);
            return;
        }

        RitualRecipe recipe = match.get();

        // Forbruk ingredienser
        setCenterItem(ItemStack.EMPTY);
        consumePedestalItems(pedestals, recipe);

        // Effekter
        serverLevel.playSound(null, worldPosition, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.8f, 1.4f);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                80, 0.6, 0.8, 0.6, 0.05);

        switch (recipe.outcome()) {
            case ITEM -> {
                ItemEntity entity = new ItemEntity(serverLevel,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                        recipe.result().copy());
                entity.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(entity);
            }
            case PORTAL -> {
                BlockPos portalPos = worldPosition.above(2);
                if (serverLevel.isEmptyBlock(portalPos)) {
                    serverLevel.setBlock(portalPos, ModBlocks.SKYGGEPORTAL.get().defaultBlockState(), 3);
                    player.displayClientMessage(Component.translatable("message.svartmagi.portal_opened"), false);
                } else {
                    player.displayClientMessage(Component.translatable("message.svartmagi.portal_blocked"), true);
                }
            }
            case SUMMON_BOSS -> {
                SkyggevokterEntity boss = ModEntities.SKYGGEVOKTER.get().create(serverLevel);
                if (boss != null) {
                    boss.moveTo(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 2.5,
                            serverLevel.random.nextFloat() * 360f, 0);
                    boss.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(worldPosition),
                            MobSpawnType.MOB_SUMMONED, null);
                    serverLevel.addFreshEntity(boss);
                    serverLevel.playSound(null, worldPosition, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.2f);
                }
            }
        }
    }

    private List<PedestalBlockEntity> findPedestals(ServerLevel serverLevel) {
        List<PedestalBlockEntity> pedestals = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -PEDESTAL_RANGE; dx <= PEDESTAL_RANGE; dx++) {
            for (int dz = -PEDESTAL_RANGE; dz <= PEDESTAL_RANGE; dz++) {
                if (dx == 0 && dz == 0) continue;
                cursor.set(worldPosition.getX() + dx, worldPosition.getY(), worldPosition.getZ() + dz);
                if (serverLevel.getBlockEntity(cursor) instanceof PedestalBlockEntity pedestal) {
                    pedestals.add(pedestal);
                }
            }
        }
        return pedestals;
    }

    private void consumePedestalItems(List<PedestalBlockEntity> pedestals, RitualRecipe recipe) {
        List<net.minecraft.world.item.crafting.Ingredient> remaining = new ArrayList<>(recipe.pedestalItems());
        for (PedestalBlockEntity pedestal : pedestals) {
            ItemStack item = pedestal.getItem();
            if (item.isEmpty()) continue;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).test(item)) {
                    remaining.remove(i);
                    pedestal.setItem(ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!centerItem.isEmpty()) {
            tag.put("CenterItem", centerItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        centerItem = tag.contains("CenterItem")
                ? ItemStack.parseOptional(registries, tag.getCompound("CenterItem"))
                : ItemStack.EMPTY;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

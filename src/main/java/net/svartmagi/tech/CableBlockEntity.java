package net.svartmagi.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.svartmagi.SvartmagiConfig;
import net.svartmagi.registry.ModBlockEntities;

/**
 * Kraftkabel: reléer FE videre til naboer hver tick, slik at generatorer
 * kan mate maskiner som ikke staar i direkte kontakt med hverandre.
 * Ingen egen GUI - bare en liten intern buffer som toemmes videre.
 */
public class CableBlockEntity extends BlockEntity {
    private final EnergyBuffer energy;

    @SuppressWarnings("unchecked")
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] neighbors = new BlockCapabilityCache[6];

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KRAFTKABEL.get(), pos, state);
        int capacity = SvartmagiConfig.CABLE_CAPACITY.get();
        int perTick = SvartmagiConfig.CABLE_TRANSFER_PER_TICK.get();
        this.energy = new EnergyBuffer(capacity, perTick, perTick, this::setChanged);
    }

    public IEnergyStorage getEnergy() {
        return energy;
    }

    public void serverTick() {
        if (!SvartmagiConfig.TECH_ENABLED.get()) return;
        int available = energy.getEnergyStored();
        if (available <= 0) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        int perTick = SvartmagiConfig.CABLE_TRANSFER_PER_TICK.get();
        for (Direction dir : Direction.values()) {
            if (available <= 0) return;
            IEnergyStorage neighbor = neighborCache(serverLevel, dir).getCapability();
            if (neighbor == null || !neighbor.canReceive()) continue;
            int accepted = neighbor.receiveEnergy(Math.min(available, perTick), false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                available -= accepted;
            }
        }
    }

    private BlockCapabilityCache<IEnergyStorage, Direction> neighborCache(ServerLevel serverLevel, Direction dir) {
        int i = dir.get3DDataValue();
        if (neighbors[i] == null) {
            neighbors[i] = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, serverLevel,
                    worldPosition.relative(dir), dir.getOpposite());
        }
        return neighbors[i];
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy.setEnergy(tag.getInt("Energy"));
    }
}

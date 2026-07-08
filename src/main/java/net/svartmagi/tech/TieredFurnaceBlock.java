package net.svartmagi.tech;

import net.svartmagi.registry.ModBlockEntities;

/** Oppgraderte ovner: samme oppskrifter som vanilla, men raskere per tier. */
public class TieredFurnaceBlock extends MachineBlock {
    public enum Tier {
        KOBBER(2.0f),
        JERN(3.0f),
        DIAMANT(6.0f);

        public final float speedMultiplier;

        Tier(float speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
        }
    }

    private final Tier tier;

    public TieredFurnaceBlock(Properties properties, Tier tier) {
        super(properties, () -> ModBlockEntities.TIER_OVN.get());
        this.tier = tier;
    }

    public Tier tier() {
        return tier;
    }
}

package net.svartmagi.tech;

import net.neoforged.neoforge.energy.EnergyStorage;

/** FE-buffer som markerer blockentityen som endret ved endringer. */
public class EnergyBuffer extends EnergyStorage {
    private final Runnable onChange;

    public EnergyBuffer(int capacity, int maxReceive, int maxExtract, Runnable onChange) {
        super(capacity, maxReceive, maxExtract);
        this.onChange = onChange;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int received = super.receiveEnergy(toReceive, simulate);
        if (received > 0 && !simulate) onChange.run();
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        int extracted = super.extractEnergy(toExtract, simulate);
        if (extracted > 0 && !simulate) onChange.run();
        return extracted;
    }

    /** Intern generering (ignorerer maxReceive). */
    public void generate(int amount) {
        int old = energy;
        energy = Math.min(capacity, energy + amount);
        if (energy != old) onChange.run();
    }

    /** Internt forbruk (ignorerer maxExtract). */
    public void consume(int amount) {
        int old = energy;
        energy = Math.max(0, energy - amount);
        if (energy != old) onChange.run();
    }

    public void setEnergy(int value) {
        this.energy = Math.max(0, Math.min(capacity, value));
    }

    public int getCapacity() {
        return capacity;
    }
}

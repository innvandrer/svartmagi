package net.svartmagi.tech;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

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

    /**
     * Visning som kun tillater uttrekk. Brukes som generatorens exposed
     * capability, saa generatorer ikke kan lades utenfra eller dytte
     * energi frem og tilbake mellom hverandre.
     */
    public IEnergyStorage extractOnlyView() {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                return EnergyBuffer.this.extractEnergy(toExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return EnergyBuffer.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return EnergyBuffer.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    /**
     * Visning som kun tillater innmating. Brukes som maskinenes exposed
     * capability: maskiner mottar strom, men kan ikke toemmes utenfra.
     */
    public IEnergyStorage receiveOnlyView() {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                return EnergyBuffer.this.receiveEnergy(toReceive, simulate);
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return EnergyBuffer.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return EnergyBuffer.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }
}

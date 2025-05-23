package com.example.virtualpet.userData;

import java.io.Serializable;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String petName;
    private int hunger;
    private int sleep;
    private int energy;

    public Pet(String petName, int hunger, int sleep, int energy) {
        this.petName = petName;
        this.hunger = hunger;
        this.sleep = sleep;
        this.energy = energy;
    }

    public String getPetName() { return petName; }
    public int getHunger() { return hunger; }
    public int getSleep() { return sleep; }
    public int getEnergy() { return energy; }

    public void setHunger(int hunger) { this.hunger = hunger; }
    public void setSleep(int sleep) { this.sleep = sleep; }
    public void setEnergy(int energy) { this.energy = energy; }

    @Override
    public String toString() {
        return "Pet{" +
                "petName='" + petName + '\'' +
                ", hunger=" + hunger +
                ", sleep=" + sleep +
                ", energy=" + energy +
                '}';
    }
}

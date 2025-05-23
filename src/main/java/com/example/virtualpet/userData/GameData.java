package com.example.virtualpet.userData;

public class GameData {
    private String username;
    private String passwordHash;
    private Pet pet;

    public GameData(String username, String passwordHash, Pet pet) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.pet = pet;
    }

    public String getUsername() {
        return username;

    }
    public String getPasswordHash() {
        return passwordHash;
    }

    public Pet getPet() {
        return pet;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\n"
                + "PasswordHash: " + passwordHash + "\n"
                + "Pet: " + pet.getPetName() + "\n"
                + "Hunger: " + pet.getHunger() + "\n"
                + "Happiness: " + pet.getSleep() + "\n"
                + "Energy: " + pet.getEnergy();
    }

    public static GameData fromString(String data) {
        String[] lines = data.split("\n");
        if (lines.length < 6) {
            throw new IllegalArgumentException("Incomplete data format!");
        }

        String username = lines[0].split(": ")[1];
        String passwordHash = lines[1].split(": ")[1];
        String petName = lines[2].split(": ")[1];
        int hunger = Integer.parseInt(lines[3].split(": ")[1]);
        int happiness = Integer.parseInt(lines[4].split(": ")[1]);
        int energy = Integer.parseInt(lines[5].split(": ")[1]);

        Pet pet = new Pet(petName, hunger, happiness, energy);
        return new GameData(username, passwordHash, pet);
    }
}

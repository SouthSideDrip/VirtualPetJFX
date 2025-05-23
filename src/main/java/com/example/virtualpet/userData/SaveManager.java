package com.example.virtualpet.userData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SaveManager {

    public static void saveGame(GameData data, String filename) {
        saveUser(filename, data);
    }

    public static GameData loadGame(String filename, String username) {
        return loadUser(filename, username);
    }

    public static List<GameData> loadAllUsers(String filename) {
        List<GameData> users = new ArrayList<>();

        if (!Files.exists(Paths.get(filename))) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder userBlock = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    if (userBlock.length() > 0) {
                        try {
                            GameData user = GameData.fromString(userBlock.toString().trim());
                            users.add(user);
                        } catch (Exception e) {
                            System.out.println("Hatalı kullanıcı verisi atlandı.");
                        }
                        userBlock.setLength(0);
                    }
                } else {
                    userBlock.append(line).append("\n");
                }
            }
            if (userBlock.length() > 0) {
                try {
                    GameData user = GameData.fromString(userBlock.toString().trim());
                    users.add(user);
                } catch (Exception e) {
                    System.out.println("Hatalı kullanıcı verisi atlandı.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static GameData loadUser(String filename, String username) {
        List<GameData> users = loadAllUsers(filename);
        for (GameData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void saveUser(String filename, GameData data) {
        List<GameData> users = loadAllUsers(filename);

        boolean userFound = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(data.getUsername())) {
                users.set(i, data);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            users.add(data);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (GameData user : users) {
                writer.write(user.toString());
                writer.write("\n---\n");
            }
            System.out.println("Dosyaya kaydedildi.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.virtualpet;

import com.example.virtualpet.userData.PasswordDialog;
import com.example.virtualpet.userData.GameData;
import com.example.virtualpet.userData.Pet;
import com.example.virtualpet.userData.SaveManager;
import com.example.virtualpet.userData.Password;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private static final String SAVE_FILE = "savedGame.dat";
    private Controller controller;
    private String currentUsername;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Ask if Login or Register
        List<String> choices = Arrays.asList("Login", "Register");
        ChoiceDialog<String> modeDialog = new ChoiceDialog<>("Login", choices);
        modeDialog.setTitle("Welcome");
        modeDialog.setHeaderText("Do you want to login or register?");
        modeDialog.setContentText("Choose:");

        Optional<String> modeResult = modeDialog.showAndWait();
        if (modeResult.isEmpty()) {
            System.exit(0);
        }

        boolean isRegistering = modeResult.get().equals("Register");

        // Ask for username
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("User");
        usernameDialog.setHeaderText("Please enter your username:");
        usernameDialog.setContentText("Username:");

        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isEmpty() || usernameResult.get().trim().isEmpty()) {
            showAlert("Error", "No username entered. The application will now close.");
            System.exit(0);
        }
        currentUsername = usernameResult.get().trim();

        // Ask for password
        PasswordDialog passwordDialog = new PasswordDialog();
        Optional<String> passwordResult = passwordDialog.showAndWait();
        if (passwordResult.isEmpty() || passwordResult.get().trim().isEmpty()) {
            showAlert("Error", "No password entered. The application will now close.");
            System.exit(0);
        }
        String enteredPassword = passwordResult.get().trim();

        // Handle login/register logic
        GameData loadedData = SaveManager.loadGame(SAVE_FILE, currentUsername);

        if (isRegistering) {
            if (loadedData != null) {
                showAlert("Error", "This username already exists. Please try logging in.");
                System.exit(0);
            }
            String hashedPass = Password.sha256(enteredPassword);
            Pet defaultPet = new Pet("Cat",
                    randomStat(3, 10),
                    randomStat(3, 10),
                    randomStat(3, 10)
            );

            loadedData = new GameData(currentUsername, hashedPass, defaultPet);
            SaveManager.saveGame(loadedData, SAVE_FILE);
            showAlert("Success", "Registration successful!");
        } else {
            if (loadedData == null) {
                showAlert("Error", "User not found. Please try registering first.");
                System.exit(0);
            }

            String storedHash = loadedData.getPasswordHash();
            String enteredHash = Password.sha256(enteredPassword);

            if (!storedHash.equals(enteredHash)) {
                showAlert("Error", "Incorrect password.");
                System.exit(0);
            }
        }

        // Load JavaFX UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("controller.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setGameData(loadedData);


        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Virtual Pet Game - " + currentUsername);
        primaryStage.show();

        primaryStage.setOnCloseRequest(this::handleSaveOnClose);
    }

    private void handleSaveOnClose(WindowEvent event) {
        SaveManager.saveGame(controller.getGameData(), SAVE_FILE);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private int randomStat(int min, int max) {
        return min + (int)(Math.random() * (max - min + 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

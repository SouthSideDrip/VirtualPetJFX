package com.example.virtualpet;

import com.example.virtualpet.userData.GameData;
import com.example.virtualpet.userData.Password;
import com.example.virtualpet.userData.Pet;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

        @FXML private ChoiceBox<String> animalChoiceBox;
        @FXML private Label selectionLabel;
        @FXML private ImageView selectedAnimalImageView;
        @FXML private AnchorPane selectionPane;
        @FXML private AnchorPane gamePane;
        @FXML private AnchorPane rootPane;
        @FXML private ImageView backgroundImageView;
        @FXML private ImageView petImageView;
        @FXML private Button feedButton;
        @FXML private Button sleepButton;
        @FXML private Button playButton;
        @FXML private Label statusLabel;
        @FXML private ProgressBar hungerProgressBar;
        @FXML private ProgressBar sleepProgressBar;
        @FXML private ProgressBar energyProgressBar;

        private String selectedAnimal;
        private Timeline timeline;
        private final double MOVE_DISTANCE = 20;
        private final double JUMP_HEIGHT = 50;
        private final double PET_WIDTH = 100;
        private final double MIN_Y = 120;
        private final double MAX_Y = 300;
        private final double RIGHT_BOUNDARY = 300;

        private GameData gameData;

        // 1: facing right, -1: facing left
        private int direction = 1;

        // Store original dimensions for resetting size
        private double originalPetImageWidth;
        private double originalPetImageHeight;


        @Override
        public void initialize(URL location, ResourceBundle resources) {
                animalChoiceBox.getItems().addAll("Cat", "Panda");
                animalChoiceBox.getSelectionModel().selectFirst();
                selectedAnimal = animalChoiceBox.getValue();

                animalChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null) {
                                selectedAnimal = newVal;
                                selectionLabel.setText("Selected: " + newVal);
                                updateAnimalImage(newVal);
                        }
                });

                updateAnimalImage(selectedAnimal);
                selectionLabel.setText("Selected: " + selectedAnimal);

                // Store original image dimensions after initial setup
                // It's better to do this after the image is set in updateAnimalImage
                // or ensure the image is loaded before attempting to get its dimensions.
                // For simplicity, we'll assume the image is loaded by the time initialize finishes.

                // You might want to get these once the petImageView has its initial image set
                // and its preferred size is determined by JavaFX.
                // A common way to get initial size is to set it explicitly in FXML or code
                // or wait for the layout pass. For now, we'll get it after setting the image.


                resetStats();

                feedButton.setOnAction(e -> {
                        if (gameData.getPet().getHunger() < 10) {
                                gameData.getPet().setHunger(gameData.getPet().getHunger() + 1);
                                updateAllProgressBars();
                                statusLabel.setText("You fed your pet! Hunger: " + gameData.getPet().getHunger());

                                double currentScale = Math.abs(petImageView.getScaleX());
                                double scaleStep = 0.05;
                                double maxScale = 2.0;
                                double newScale = currentScale + scaleStep;

                                if (newScale <= maxScale) {
                                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), petImageView);
                                        scaleTransition.setToX(direction * newScale); // Keep direction
                                        scaleTransition.setToY(newScale);
                                        scaleTransition.play();
                                }
                        } else {
                                statusLabel.setText("Your pet is full! 😊");
                        }
                });

                sleepButton.setOnAction(e -> {
                        if (gameData.getPet().getSleep() < 10) {
                                gameData.getPet().setSleep(gameData.getPet().getSleep() + 1);
                                updateAllProgressBars();
                                statusLabel.setText("Your pet slept! Sleep: " + gameData.getPet().getSleep());
                        } else {
                                statusLabel.setText("Your pet is fully rested! 🛌");
                        }
                });

                playButton.setOnAction(e -> {
                        if (gameData.getPet().getEnergy() < 10) {
                                gameData.getPet().setEnergy(gameData.getPet().getEnergy() + 1);
                                updateAllProgressBars();
                                statusLabel.setText("Your pet played! Energy: " + gameData.getPet().getEnergy());
                        } else {
                                statusLabel.setText("Your pet is fully energized! ⚡");
                        }
                });

                timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                        Pet pet = gameData.getPet();
                        if (pet.getHunger() > 0) pet.setHunger(pet.getHunger() - 1);
                        if (pet.getSleep() > 0) pet.setSleep(pet.getSleep() - 1);
                        if (pet.getEnergy() > 0) pet.setEnergy(pet.getEnergy() - 1);
                        updateAllProgressBars();
                        if (pet.getHunger() == 0 || pet.getSleep() == 0 || pet.getEnergy() == 0) {
                                statusLabel.setText("Your pet needs attention!");
                        }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);

                rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null) {
                                newScene.setOnKeyPressed(this::handleKeyPress);
                        }
                });
        }

        public void setGameData(GameData data) {
                this.gameData = data;
                this.selectedAnimal = data.getPet().getPetName();
                selectionLabel.setText("Selected: " + selectedAnimal);
                updateAnimalImage(selectedAnimal);
                updateAllProgressBars();
        }

        private int randomStat(int min, int max) {
                return min + (int)(Math.random() * (max - min + 1));
        }

        private void resetStats() {
                int randomHunger = randomStat(5, 10);
                int randomSleep = randomStat(5, 10);
                int randomEnergy = randomStat(5, 10);
                this.gameData = new GameData(
                        "DefaultUser",
                        Password.sha256("default"),
                        new Pet(selectedAnimal, randomHunger, randomSleep, randomEnergy)
                );
                updateAllProgressBars();
        }

        private void updateAllProgressBars() {
                Pet pet = gameData.getPet();
                hungerProgressBar.setProgress(pet.getHunger() / 10.0);
                sleepProgressBar.setProgress(pet.getSleep() / 10.0);
                energyProgressBar.setProgress(pet.getEnergy() / 10.0);

                // Shrink pet if stats are low
                if (pet.getHunger() < 3 && pet.getSleep() < 3 && pet.getEnergy() < 3) {
                        double currentScale = Math.abs(petImageView.getScaleX());
                        double minScale = 0.5;
                        double shrinkStep = 0.05;

                        if (currentScale > minScale) {
                                ScaleTransition shrink = new ScaleTransition(Duration.millis(300), petImageView);
                                shrink.setToX(direction * (currentScale - shrinkStep)); // Keep direction
                                shrink.setToY(currentScale - shrinkStep);
                                shrink.play();
                        }
                }
                // Reset to original size if all stats are zero
                if (pet.getHunger() == 0 && pet.getSleep() == 0 && pet.getEnergy() == 0) {
                        statusLabel.setText("Your pet is very unhappy and has shrunk!");
                        ScaleTransition resetSize = new ScaleTransition(Duration.millis(500), petImageView);
                        resetSize.setToX(direction * 1.0); // Reset to original size, keeping direction
                        resetSize.setToY(1.0);
                        resetSize.play();
                }
        }

        public void updateAnimalImage(String animal) {
                String imagePath;
                switch (animal) {
                        case "Cat" -> imagePath = "/CatImage.png";
                        case "Panda" -> imagePath = "/PandaImage.png";
                        default -> imagePath = null;
                }
                if (imagePath != null) {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        selectedAnimalImageView.setImage(image);
                        petImageView.setImage(image);

                        // Store original dimensions when the image is first set
                        // This is crucial for resetting later
                        originalPetImageWidth = image.getWidth();
                        originalPetImageHeight = image.getHeight();
                        // You might want to set fitWidth/fitHeight on the ImageView if you want
                        // to control its initial displayed size in the scene, and then scale from there.
                        // For example:
                        // petImageView.setFitWidth(PET_WIDTH);
                        // petImageView.setPreserveRatio(true);
                }
        }

        @FXML
        public void handleStartButton() {
                if (selectedAnimal == null) {
                        selectionLabel.setText("Please select an animal before playing!");
                        return;
                }

                selectionPane.setVisible(false);
                selectionPane.setManaged(false);

                gamePane.setVisible(true);
                gamePane.setManaged(true);

                backgroundImageView.setImage(new Image(getClass().getResourceAsStream("/background.jpg")));

                if (gameData == null || gameData.getPet() == null) {
                        resetStats();
                } else {
                        updateAllProgressBars();
                }

                statusLabel.setText("Game started! Take care of your pet.");

                petImageView.setLayoutX(20);
                petImageView.setLayoutY(MAX_Y);

                timeline.play();
        }

        private void handleKeyPress(KeyEvent event) {
                switch (event.getCode()) {
                        case W -> jumpPet(petImageView);
                        case S -> moveVertical(petImageView, MOVE_DISTANCE);
                        case A -> moveHorizontal(petImageView, -MOVE_DISTANCE);
                        case D -> moveHorizontal(petImageView, MOVE_DISTANCE);
                }
        }

        private void jumpPet(ImageView pet) {
                if (pet.getLayoutY() - JUMP_HEIGHT < MIN_Y) return;
                TranslateTransition jump = new TranslateTransition(Duration.millis(300), pet);
                jump.setByY(-JUMP_HEIGHT);
                jump.setAutoReverse(true);
                jump.setCycleCount(2);
                jump.setOnFinished(e -> {
                        pet.setLayoutY(pet.getLayoutY() - JUMP_HEIGHT);
                        pet.setTranslateY(0);
                });
                jump.play();
        }

        private void moveVertical(ImageView pet, double dy) {
                double currentY = pet.getLayoutY();
                double newY = Math.max(MIN_Y, Math.min(MAX_Y, currentY + dy));
                TranslateTransition move = new TranslateTransition(Duration.millis(150), pet);
                move.setToY(newY - currentY);
                move.setOnFinished(e -> {
                        pet.setLayoutY(newY);
                        pet.setTranslateY(0);
                });
                move.play();
        }

        private void moveHorizontal(ImageView pet, double dx) {
                double currentX = pet.getLayoutX();
                double tempX = currentX + dx;

                // Update direction
                direction = dx < 0 ? -1 : 1;

                // Get absolute scale and apply direction
                double currentScale = Math.abs(pet.getScaleX());
                pet.setScaleX(direction * currentScale);

                double scaledWidth = PET_WIDTH * currentScale; // Assuming PET_WIDTH is the base width for scaling
                double newX = Math.max(0, Math.min(RIGHT_BOUNDARY - scaledWidth, tempX));

                TranslateTransition move = new TranslateTransition(Duration.millis(150), pet);
                move.setToX(newX - currentX);
                move.setOnFinished(e -> {
                        pet.setLayoutX(newX);
                        pet.setTranslateX(0);
                });
                move.play();
        }
        public GameData getGameData() {
                return gameData;
        }
}
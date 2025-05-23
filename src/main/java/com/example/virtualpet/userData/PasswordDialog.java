package com.example.virtualpet.userData;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * Custom dialog for password input using JavaFX.
 * The password is masked (not visible while typing).
 */
public class PasswordDialog extends Dialog<String> {

    public PasswordDialog() {
        setTitle("Password Input");
        setHeaderText("Please enter your password:");

        // Create password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        GridPane.setHgrow(passwordField, Priority.ALWAYS);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);

        getDialogPane().setContent(grid);

        // Buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Return result when OK is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return passwordField.getText();
            }
            return null;
        });
    }
}

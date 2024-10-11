package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLOutput;
import java.util.Objects;

public class RegisterController {

    @FXML
    private TextField fullNameField;
    @FXML
    private Label fullNameNotificationLabel;

    @FXML
    private TextField phoneNumberField;
    @FXML
    private Label phoneNumberNotificationLabel;

    @FXML
    private TextField emailField;
    @FXML
    private Label emailNotificationLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private Label usernameNotificationLabel;

    @FXML
    private TextField passwordField;
    @FXML
    private Label passwordNotificationLabel;

    @FXML
    private TextField confirmPasswordField;
    @FXML
    private Label confirmPasswordNotificationLabel;

    @FXML
    private Label submitRegistrationResult;

    private final DatabaseConnection dbConnection = new DatabaseConnection();

    /**
     * Switch from Register Scene to Login Scene by click Return to Log in button.
     * @param event will read event when you click on Register button.
     */
    @FXML
    public void returnToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Views/login.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 960, 640));
        stage.show();
    }

    @FXML
    public void submitRegistration(ActionEvent event) {

        boolean valid = true;

        fullNameNotificationLabel.setText("");
        phoneNumberNotificationLabel.setText("");
        emailNotificationLabel.setText("");
        usernameNotificationLabel.setText("");
        passwordNotificationLabel.setText("");
        confirmPasswordNotificationLabel.setText("");

        submitRegistrationResult.setText("");

        if (fullNameField.getText().isEmpty()) {
            fullNameNotificationLabel.setText("Full Name is required");
            valid = false;
        } else if (!checkFullNameValidation()) {
            fullNameNotificationLabel.setText("Invalid Full Name");
            valid = false;
        }

        if (phoneNumberField.getText().isEmpty()) {
            phoneNumberNotificationLabel.setText("Phone Number is required");
            valid = false;
        } else if (!checkPhoneNumberValidation()) {
            phoneNumberNotificationLabel.setText("Invalid Phone Number");
            valid = false;
        }

        if (usernameField.getText().isEmpty()) {
            usernameNotificationLabel.setText("Username is required");
            valid = false;
        } else if (!checkUsernameValidation()) {
            usernameNotificationLabel.setText("Username must not have special characters");
            valid = false;
        } else if (!checkUsernameAvailable()) {
            usernameNotificationLabel.setText("Username already exists. Please try another username");
            valid = false;
        }

        if (emailField.getText().isEmpty()) {
            emailNotificationLabel.setText("Email is required");
            valid = false;
        } else if (!checkEmailValidation()) {
            emailNotificationLabel.setText("Invalid Email");
            valid = false;
        }

        if (passwordField.getText().isEmpty()) {
            passwordNotificationLabel.setText("Password is required");
            valid = false;
        } else if (!checkPasswordValidation()) {
            passwordNotificationLabel.setText("Password must have at least 8 characters and " +
                    "\nmust not contain a consecutive series of numbers");
            valid = false;
        }

        if (!checkConfirmPasswordValidation()) {
            confirmPasswordNotificationLabel.setText("Password does not match");
            valid = false;
        }

        String resultNotification = valid ? "Create account successfully!" : "Create account failed!";
        submitRegistrationResult.setText(resultNotification);
    }

    /**
     * Check Username available when user register.
     * @return true if username doesn't exist and false in the opposite case.
     */
    private boolean checkUsernameAvailable() {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usernameField.getText());
            ResultSet rs = stmt.executeQuery();

            return !rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate the full name. The full name should only contain letters.
     *
     * @return true if the full name is valid, false otherwise.
     */
    private boolean checkFullNameValidation() {
        String fullName = fullNameField.getText();
        int n = fullName.length();

        for (int i = 0; i < n; i++) {
            if (!Character.isLetter(fullName.charAt(i)) && fullName.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate the phone number. The phone number should only contain digits.
     *
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean checkPhoneNumberValidation() {
        String phoneNumber = phoneNumberField.getText();
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate the email format. The email must contain "@" and at least one "."
     * after the "@".
     *
     * @return true if the email format is valid, false otherwise.
     */
    private boolean checkEmailValidation() {
        String email = emailField.getText();
        int atIndex = email.indexOf('@');
        return atIndex > 0 && email.indexOf('.', atIndex) > atIndex + 1;
    }

    /**
     * Validate the username. The username should not contain special characters.
     *
     * @return true if the username is valid, false otherwise.
     */
    private boolean checkUsernameValidation() {
        String username = usernameField.getText();
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate the password. The password must have at least 8 characters and must
     * not contain a sequence of 3 or more consecutive digits.
     *
     * @return true if the password is valid, false otherwise.
     */
    private boolean checkPasswordValidation() {
        String password = passwordField.getText();
        if (password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        int consecutiveIncreaseCount = 0;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isLetter(c)) {
                hasLetter = true;
                consecutiveIncreaseCount = 0;
            } else if (Character.isDigit(c)) {
                if (i > 0 && Character.isDigit(password.charAt(i - 1))) {
                    if (password.charAt(i) - password.charAt(i - 1) == 1) {
                        consecutiveIncreaseCount++;
                        if (consecutiveIncreaseCount >= 2) {
                            return false;
                        }
                    } else {
                        consecutiveIncreaseCount = 0;
                    }
                }
            }
        }

        return hasLetter;
    }

    /**
     * Validate if the confirmation password matches the password.
     *
     * @return true if the confirmation password matches the password, false otherwise.
     */
    private boolean checkConfirmPasswordValidation() {
        return passwordField.getText().equals(confirmPasswordField.getText());
    }
}

package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginAndRegisterController implements Initializable {

    @FXML
    private AnchorPane sliderContainer;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private AnchorPane registerForm;

    @FXML
    private Label loginNotificationLabel;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField loginUsername;

    @FXML
    private TextField registerFullName;
    @FXML
    private Label fullNameNotificationLabel;

    @FXML
    private TextField registerUsername;
    @FXML
    private Label usernameNotificationLabel;

    @FXML
    private PasswordField registerPassword;
    @FXML
    private Label passwordNotificationLabel;

    @FXML
    private PasswordField registerConfirmPassword;
    @FXML
    private Label confirmPasswordNotificationLabel;

    @FXML
    private Label registerNotificationLabel;

    /**
     *Set view of container.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rectangle clip = new Rectangle(800, 480);
        clip.setLayoutX(0);
        clip.setLayoutY(0);
        sliderContainer.setClip(clip);
    }

    /**
     * Check valid of user information.
     * @param event read event.
     */
    @FXML
    protected void handleLoginAction(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                return checkLoginValidity(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            if (loginTask.getValue()) {
                loginNotificationLabel.setText("Login Success!");
            } else {
                loginPassword.clear();
                loginNotificationLabel.setText("Login Failed. Please check your credentials.");
            }
            executorService.shutdown();
        });

        executorService.submit(loginTask);
    }

    /**
     * checkLogin checks the correctness of the user information requested to log in.
     * @param username username account.
     * @param password user account password.
     * @return true if user information is correct and false when user information incorrect.
     */
    private boolean checkLoginValidity(String username, String password) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
            return false;
        }

        String connectQuery = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // result is not null
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            return false;
        }

        return false;
    }

    /**
     * Check valid register information.
     * @param event read event.
     */
    @FXML
    protected void handleRegisterAction(ActionEvent event) {
        boolean valid = true;

        if (registerFullName.getText().isEmpty()) {
            fullNameNotificationLabel.setText("* Full Name is required");
            valid = false;
        } else if (!checkFullNameValidation(registerFullName.getText())) {
            fullNameNotificationLabel.setText("* Full name must not contain number.");
            valid = false;
        }

        if (registerUsername.getText().isEmpty()) {
            usernameNotificationLabel.setText("* Username is required");
            valid = false;
        } else if (!checkUsernameValidation(registerUsername.getText())) {
            usernameNotificationLabel.setText("* Username must not have special characters");
            valid = false;
        } else if (!checkUsernameAvailable(registerUsername.getText())) {
            usernameNotificationLabel.setText("* Username already exists. Please try another username");
            valid = false;
        }

        if (registerPassword.getText().isEmpty()) {
            passwordNotificationLabel.setText("* Password is required");
            valid = false;
        } else if (!checkPasswordValidation(registerPassword.getText())) {
            passwordNotificationLabel.setText("* Password must have at least 8 characters and " +
                    "\nmust not contain a consecutive series of numbers");
            valid = false;
        }

        if (!checkConfirmPasswordValidation(registerConfirmPassword.getText())) {
            confirmPasswordNotificationLabel.setText("* Password does not match");
            valid = false;
        }

       registerNotificationLabel.setText(valid ? "Success!" : "Create account fail! Please try again.");
    }

    /**
     * Validate the full name. The full name should only contain letters.
     *
     * @return true if the full name is valid, false otherwise.
     */
    private boolean checkFullNameValidation(String fullName) {
        for (int i = 0; i < fullName.length(); i++) {
            if (!Character.isLetter(fullName.charAt(i)) && fullName.charAt(i) != ' ') {
                return false;
            }
        }

        return true;
    }

    /**
     * Check Username available when user register.
     *
     * @return true if username doesn't exist and false in the opposite case.
     */
    private boolean checkUsernameValidation(String username) {
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check Username available when user register.
     * @return true if username doesn't exist and false in the opposite case.
     */
    private boolean checkUsernameAvailable(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return !rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate the password. The password must have at least 8 characters and must
     * not contain a sequence of 3 or more consecutive digits.
     *
     * @return true if the password is valid, false otherwise.
     */
    private boolean checkPasswordValidation(String password) {
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
    private boolean checkConfirmPasswordValidation(String confirmPassword) {
        return registerPassword.getText().equals(registerConfirmPassword.getText());
    }

    /**
     * Animation sliding to register form.
     */
    @FXML
    public void goToRegister() {

        TranslateTransition registerFormSliding = new TranslateTransition();
        registerFormSliding.setNode(registerForm);
        registerFormSliding.setDuration(Duration.seconds(0.5));
        registerFormSliding.setToX(-400);
        registerFormSliding.play();

        TranslateTransition loginFormSliding = new TranslateTransition();
        loginFormSliding.setNode(loginForm);
        loginFormSliding.setDuration(Duration.seconds(0.5));
        loginFormSliding.setToX(-400);
        loginFormSliding.play();

    }

    /**
     * Animation sliding to log in form.
     */
    @FXML
    public void goToLogin() {

        TranslateTransition loginFormSliding = new TranslateTransition();
        loginFormSliding.setNode(loginForm);
        loginFormSliding.setDuration(Duration.seconds(0.5));
        loginFormSliding.setToX(0);
        loginFormSliding.play();

        TranslateTransition registerFormSliding = new TranslateTransition();
        registerFormSliding.setNode(registerForm);
        registerFormSliding.setDuration(Duration.seconds(0.5));
        registerFormSliding.setToX(400);
        registerFormSliding.play();

    }
}
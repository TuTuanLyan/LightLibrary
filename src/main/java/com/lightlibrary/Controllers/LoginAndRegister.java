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

public class LoginAndRegister implements Initializable {

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
    private Label usenameNotitficationLabel;

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

        String registerResult = valid ? "Success!" : "Create account fail!";
    }

    private boolean checkFullNameValidation(String fullName) {
        for (int i = 0; i < fullName.length(); i++) {
            if (!Character.isLetter(fullName.charAt(i)) && fullName.charAt(i) != ' ') {
                return false;
            }
        }

        return true;
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

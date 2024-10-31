package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.User;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
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
     * Set view of container.
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
     *
     * @param event read event.
     */
    @FXML
    protected void handleLoginAction(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return checkLoginValidity(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            if (user != null) {
                loginNotificationLabel.setText("Login Success!");
                if (user instanceof Customer) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lightlibrary/Views/UserDashboard.fxml"));
                        Parent dashboard = loader.load();

                        // Chuyển đối tượng Customer vào controller của UserDashboard
                        UserDashboardController controller = loader.getController();
                        controller.setCustomer((Customer) user);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(dashboard, 960, 640));
                        stage.show();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                /*if (role.equalsIgnoreCase("CUSTOMER")) {
                    try {
                        Parent dashboard = FXMLLoader.load(Objects.requireNonNull(getClass()
                                .getResource("/com/lightlibrary/Views/UserDashboard.fxml")));
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(dashboard, 960, 640));
                        stage.show();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (role.equalsIgnoreCase("ADMIN")) {
                    loginNotificationLabel.setText("Welcome Admin!");
                }*/
                executorService.shutdown();
            } else {
                loginPassword.clear();
                loginNotificationLabel.setText("Login Failed. Please check your credentials.");
                executorService.shutdown();
            }
        });

        executorService.submit(loginTask);
    }

    /**
     * checkLogin checks the correctness of the user information requested to log in.
     *
     * @param username username account.
     * @param password user account password.
     * @return "CUSTOMER", "ADMIN" if valid login; null if login fails.
     */
    private User checkLoginValidity(String username, String password) {
        Connection connectDB = DatabaseConnection.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
            return null;
        }

        String connectQuery = "SELECT * FROM users WHERE BINARY username = ? AND BINARY password = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if (role.equalsIgnoreCase("CUSTOMER")) {
                    Customer customer = new Customer();
                    customer.setRole(User.Role.CUSTOMER);
                    customer.setUserID(resultSet.getInt("userID"));
                    customer.setFullName(resultSet.getString("fullName"));
                    customer.setUsername(resultSet.getString("username"));
                    customer.setPassword(resultSet.getString("password"));
                    return customer;
                } else if (role.equalsIgnoreCase("ADMIN")) {

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            return null;
        }

        return null;
    }

    /**
     * Check valid register information.
     *
     * @param event read event.
     */
    @FXML
    protected void handleRegisterAction(ActionEvent event) {
        boolean valid = true;

        fullNameNotificationLabel.setText("");
        usernameNotificationLabel.setText("");
        passwordNotificationLabel.setText("");
        confirmPasswordNotificationLabel.setText("");
        registerNotificationLabel.setText("");

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
     * @param fullName full string need to check.
     * @return true if the full name don't have any number, false otherwise.
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
     * @param username username string need to check.
     * @return true if username is valid and false in the opposite case.
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
     *
     * @param username username string need to check.
     * @return true if username doesn't exist and false in the opposite case.
     */
    private boolean checkUsernameAvailable(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE BINARY username = ?";
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
     * @param password password string need to check.
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
     * @param confirmPassword confirm password string need to check.
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

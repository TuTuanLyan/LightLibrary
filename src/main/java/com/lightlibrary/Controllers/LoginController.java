package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {

    @FXML
    private Label loginResult;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    /**
     * Check valid of user information.
     * @param event read event.
     */
    @FXML
    protected void loginAccepted(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (checkLogin(username, password)) {
            loginResult.setText("Login Success!");
        } else {
            loginResult.setText("Login Failed. Please check your credentials.");
        }
    }

    /**
     * checkLogin checks the correctness of the user information requested to log in.
     * @param username username account.
     * @param password user account password.
     * @return true if user information is correct and false when user information incorrect.
     */
    private boolean checkLogin(String username, String password) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        if (connectDB == null) {
            System.out.println("Something went wrong connectDB is null!");
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
        }

        return false;
    }

    /**
     * Switch from Login Scene to Register Scene by click Register button.
     * @param event will read event when you click on Register button.
     */
    public void goToRegister(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Views/register.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 960, 640));
        stage.show();
    }
}
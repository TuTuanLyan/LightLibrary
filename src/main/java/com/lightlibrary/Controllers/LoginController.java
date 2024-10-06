package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private Label loginResult;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    /**
     * Check valid of user infor
     * @param event
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
     * checkLogin checks the correctness of the user information requested to log in
     * @param username username account
     * @param password user account password
     * @return true if user information is correct and false when user information incorrect
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
}
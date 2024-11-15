package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewUser implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane editPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reLoad();
    }

    public void reLoad() {
        while (gridPane.getChildren().size() >= 8) {
            gridPane.getChildren().remove(gridPane.getChildren().size() - 1);
        }
        Connection connectDB = DatabaseConnection.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        String connectQuery = "SELECT * FROM users";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String fullname = resultSet.getString("fullname");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("email");
                add(userID, fullname, username, password, phoneNumber, email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*for (Node node : gridPane.getChildren()){
            node.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        }*/
    }

    public void add(int userID, String fullname, String username, String password, String phoneNumber, String email) {
        int index = gridPane.getRowCount();
        // đống label dùng để nhét vào từng row
        Label userIDLabel = new Label(Integer.toString(userID));
        Label fullnameLabel = new Label(fullname);
        Label usernameLabel = new Label(username);
        Label passwordLabel = new Label(password);
        Label phoneNumberLabel = new Label(phoneNumber);
        Label emailLabel = new Label(email);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");


        gridPane.addRow(gridPane.getRowCount(), userIDLabel, fullnameLabel, usernameLabel, passwordLabel, phoneNumberLabel, emailLabel, editButton, deleteButton);

        deleteButton.setOnAction(e -> {
            remove(userID);
        });
        editButton.setOnAction(e -> {
            AtomicBoolean changed = new AtomicBoolean(false);
            editPane.setVisible(true);
            TextField editFullname = (TextField) editPane.getChildren().get(0);
            editFullname.setText(fullname);
            editFullname.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            TextField editPassword = (TextField) editPane.getChildren().get(1);
            editPassword.setText(password);
            editPassword.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            TextField editPhoneNumber = (TextField) editPane.getChildren().get(2);
            editPhoneNumber.setText(phoneNumber);
            editPhoneNumber.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            TextField editEmail = (TextField) editPane.getChildren().get(3);
            editEmail.setText(email);
            editEmail.setOnKeyTyped(keyEvent -> {
                changed.set(true);
            });
            Button ok = (Button) editPane.getChildren().get(8);
            ok.setOnAction(event -> {
                if (!changed.get()) {
                    System.out.println("Nothing change");
                } else
                    edit(userID, editFullname.getText(), editPassword.getText(), editPhoneNumber.getText(), editEmail.getText());
                editPane.setVisible(false);
            });
        });
    }

    public void remove(int userID) {
        Connection connectDB = DatabaseConnection.getConnection();
        String removeQuery = "DELETE FROM users WHERE userID = ?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(removeQuery);
            preparedStatement.setInt(1, userID);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("User deleted successfully");
            } else System.out.println("Something went wrong");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reLoad();
    }

    public void edit(int userID, String fullname, String password, String phoneNumber, String email) {
        Connection connectDB = DatabaseConnection.getConnection();
        String editQuery = "UPDATE users SET fullName = ?,password = ?,phoneNumber = ?,email = ? where userID = ?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(editQuery);
            preparedStatement.setString(1, fullname);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, email);
            preparedStatement.setInt(5, userID);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("User edited successfully");
            } else System.out.println("Something went wrong");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reLoad();

    }
}

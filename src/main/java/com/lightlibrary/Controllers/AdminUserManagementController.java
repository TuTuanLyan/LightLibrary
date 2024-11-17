package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminUserManagementController implements Initializable, SyncAction {

    @FXML
    private AnchorPane userManagementRoot;

    @FXML
    private GridPane manageUserGrid;

    @FXML
    private GridPane titleGrid;

    @FXML
    private AnchorPane editPane;

    @FXML
    private TextField sortEmail;

    @FXML
    private TextField sortFullname;

    @FXML
    private TextField sortPassword;

    @FXML
    private TextField sortPhone;

    @FXML
    private TextField sortUserID;

    @FXML
    private TextField sortUsername;

    AdminDashboardController parentController;

    public AdminDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reLoad();
        sortUserID.setOnKeyTyped(keyEvent ->reLoad());
        sortFullname.setOnKeyTyped(keyEvent ->reLoad());
        sortPassword.setOnKeyTyped(keyEvent ->reLoad());
        sortUsername.setOnKeyTyped(keyEvent ->reLoad());
        sortEmail.setOnKeyTyped(keyEvent ->reLoad());
        sortPhone.setOnKeyTyped(keyEvent ->reLoad());
    }

    @Override
    public void setTheme(boolean darkMode) {
        userManagementRoot.getStylesheets().clear();
        if (darkMode) {
            userManagementRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            userManagementRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {}


    public void reLoad() {
        while (manageUserGrid.getChildren().size() > 1) {
            manageUserGrid.getChildren().removeLast();
        }
        manageUserGrid.getRowConstraints().clear();
        String findFullname = sortFullname.getText();
        String findUsername = sortUsername.getText();
        String findPassword = sortPassword.getText();
        String findPhone = sortPhone.getText();
        String findEmail = sortEmail.getText();
        Connection connectDB = DatabaseConnection.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
        }

        if(!sortPhone.getText().isEmpty()) {
            if(!sortUserID.getText().isEmpty()) {
                int findUserID = -1;
                try {
                    findUserID = Integer.parseInt(sortUserID.getText());
                } catch (NumberFormatException e) {
                    System.out.println("wrong userID");
                }
                String connectQuery = "SELECT * FROM users WHERE userID like ? and fullName LIKE ? and username LIKE ? and password LIKE ? and phoneNumber LIKE ? and email LIKE ?";

                try {
                    PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                    preparedStatement.setInt(1, findUserID);
                    preparedStatement.setString(2, "%" + findFullname + "%");
                    preparedStatement.setString(3, "%" + findUsername + "%");
                    preparedStatement.setString(4, "%" + findPassword + "%");
                    preparedStatement.setString(5, "%" + findPhone + "%");
                    preparedStatement.setString(6, "%" + findEmail + "%");
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet == null) {
                        System.out.println("find someone with: userID=" + findUserID + " and phone:" + findPhone);
                    }
                    else System.out.println("find no one with: userID=" + findUserID + " and phone:" + findPhone);
                    while (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        String fullname = resultSet.getString("fullName");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");
                        System.out.println(userID + " " + fullname + " " + username + " " + password + " " + phoneNumber + " " + email);
                        add(userID, fullname, username, password, phoneNumber, email);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                String connectQuery = "SELECT * FROM users WHERE fullName LIKE ? and username LIKE ? and password LIKE ? and phoneNumber LIKE ? and email LIKE ?;";

                try {
                    PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                    preparedStatement.setString(1, "%" + findFullname + "%");
                    preparedStatement.setString(2, "%" + findUsername + "%");
                    preparedStatement.setString(3, "%" + findPassword + "%");
                    preparedStatement.setString(4, "%" + findPhone + "%");
                    preparedStatement.setString(5, "%" + findEmail + "%");

                    System.out.println(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet == null) {
                        System.out.println("find someone with phone: " + findPhone);
                    }
                    else System.out.println("find no one with phone: " + findPhone);
                    while (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        String fullname = resultSet.getString("fullName");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");
                        System.out.println(userID + " " + fullname + " " + username + " " + password + " " + phoneNumber + " " + email);


                        add(userID, fullname, username, password, phoneNumber, email);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        /*for (Node node : gridPane.getChildren()){
            node.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        }*/
        }
        else {
            if(!sortUserID.getText().isEmpty()) {
                int findUserID = 0;
                try {
                    findUserID = Integer.parseInt(sortUserID.getText());
                } catch (NumberFormatException e) {
                    System.out.println("wrong userID");
                }
                String connectQuery = "SELECT * FROM users WHERE userID like ? and fullName LIKE ? and username LIKE ? and password LIKE ? and email LIKE ?";

                try {
                    PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                    preparedStatement.setInt(1, findUserID);
                    preparedStatement.setString(2, "%" + findFullname + "%");
                    preparedStatement.setString(3, "%" + findUsername + "%");
                    preparedStatement.setString(4, "%" + findPassword + "%");
                    preparedStatement.setString(5, "%" + findEmail + "%");

                    System.out.println(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet == null) {
                        System.out.println("find someone");
                    }
                    else System.out.println("find nothing");
                    while (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        String fullname = resultSet.getString("fullName");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");
                        System.out.println(userID + " " + fullname + " " + username + " " + password + " " + phoneNumber + " " + email);
                        add(userID, fullname, username, password, phoneNumber, email);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                String connectQuery = "SELECT * FROM users WHERE fullName LIKE ? and username LIKE ? and password LIKE ? and email LIKE ?;";

                try {
                    PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery);
                    preparedStatement.setString(1, "%" + findFullname + "%");
                    preparedStatement.setString(2, "%" + findUsername + "%");
                    preparedStatement.setString(3, "%" + findPassword + "%");
                    preparedStatement.setString(4, "%" + findEmail + "%");

                    System.out.println(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet == null) {
                        System.out.println("find someone");
                    }
                    else System.out.println("find nothing");
                    while (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        String fullname = resultSet.getString("fullName");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");
                        System.out.println(userID + " " + fullname + " " + username + " " + password + " " + phoneNumber + " " + email);
                        add(userID, fullname, username, password, phoneNumber, email);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        /*for (Node node : gridPane.getChildren()){
            node.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        }*/
        }

    }

    public void add(int userID, String fullname, String username, String password, String phoneNumber, String email) {
        int index = manageUserGrid.getRowCount();
        // đống label dùng để nhét vào từng row
        Label userIDLabel = new Label(Integer.toString(userID));
        Label fullnameLabel = new Label(fullname);
        Label usernameLabel = new Label(username);
        Label passwordLabel = new Label(password);
        Label phoneNumberLabel = new Label(phoneNumber);
        Label emailLabel = new Label(email);
        Button editButton = new Button("Edit");
        editButton.setPrefSize(70, 30);
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefSize(70, 30);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(70);
        manageUserGrid.getRowConstraints().add(rowConstraints);
        manageUserGrid.addRow(manageUserGrid.getRowCount()-1, userIDLabel, fullnameLabel, usernameLabel, passwordLabel, phoneNumberLabel, emailLabel, editButton, deleteButton);


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
        String removeQuery = "DELETE FROM users WHERE userID = ?;";
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
        String editQuery = "UPDATE users SET fullName = ?,password = ?,phoneNumber = ?,email = ? where userID = ?;";
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

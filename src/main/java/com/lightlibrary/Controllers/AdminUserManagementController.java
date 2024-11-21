package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminUserManagementController implements Initializable, SyncAction {

    @FXML
    private AnchorPane userManagementRoot;
    @FXML
    private GridPane manageUserGrid;
    @FXML
    private TextField sortUserID, sortFullname, sortUsername, sortPassword, sortPhone, sortEmail;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField editEmail;

    @FXML
    private TextField editFullName;

    @FXML
    private AnchorPane editPane;

    @FXML
    private TextField editPassword;

    @FXML
    private TextField editPhoneNumber;

    AdminDashboardController parentController;

    public AdminDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {
        this.parentController = parentController;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFilters();
        reLoad();
    }

    private void setupFilters() {
        TextField[] filters = {sortUserID, sortFullname, sortUsername, sortPassword, sortPhone, sortEmail};
        for (TextField filter : filters) {
            filter.textProperty().addListener((observable, oldValue, newValue) -> {
                reLoad();
            });
        }
    }

    public void reLoad() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Clear the grid before reloading new data
                Platform.runLater(() -> clearGrid());

                String query = buildQuery();
                try (Connection connectDB = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                    setQueryParameters(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        String fullName = resultSet.getString("fullName");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String phone = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");

                        Platform.runLater(() -> addRow(userID, fullName, username, password, phone, email));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        // Run the task on a background thread
        new Thread(task).start();
    }

    private String buildQuery() {
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE 1=1");
        if (!sortUserID.getText().isEmpty()) query.append(" AND userID LIKE ?");
        if (!sortFullname.getText().isEmpty()) query.append(" AND fullName LIKE ?");
        if (!sortUsername.getText().isEmpty()) query.append(" AND username LIKE ?");
        if (!sortPassword.getText().isEmpty()) query.append(" AND password LIKE ?");
        if (!sortPhone.getText().isEmpty()) query.append(" AND phoneNumber LIKE ?");
        if (!sortEmail.getText().isEmpty()) query.append(" AND email LIKE ?");
        return query.toString();
    }

    private void setQueryParameters(PreparedStatement preparedStatement) throws SQLException {
        int index = 1;
        if (!sortUserID.getText().isEmpty()) preparedStatement.setString(index++, "%" + sortUserID.getText() + "%");
        if (!sortFullname.getText().isEmpty()) preparedStatement.setString(index++, "%" + sortFullname.getText() + "%");
        if (!sortUsername.getText().isEmpty()) preparedStatement.setString(index++, "%" + sortUsername.getText() + "%");
        if (!sortPassword.getText().isEmpty()) preparedStatement.setString(index++, "%" + sortPassword.getText() + "%");
        if (!sortPhone.getText().isEmpty()) preparedStatement.setString(index++, "%" + sortPhone.getText() + "%");
        if (!sortEmail.getText().isEmpty()) preparedStatement.setString(index, "%" + sortEmail.getText() + "%");
    }

    private void clearGrid() {
        manageUserGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        manageUserGrid.getRowConstraints().removeIf(row -> manageUserGrid.getRowConstraints().indexOf(row) > 0);
    }

    private void addRow(int userID, String fullname, String username, String password, String phone, String email) {
        Label userIDLabel = new Label(String.valueOf(userID));
        Label fullnameLabel = new Label(fullname);
        Label usernameLabel = new Label(username);
        Label passwordLabel = new Label(password);
        Label phoneLabel = new Label(phone);
        Label emailLabel = new Label(email);
        Button editButton = createEditButton(userID, fullname, password, phone, email);
        Button deleteButton = createDeleteButton(userID);

        int rowIndex = manageUserGrid.getRowCount();
        manageUserGrid.getRowConstraints().add(new RowConstraints(70));
        manageUserGrid.addRow(rowIndex, userIDLabel, fullnameLabel, usernameLabel, passwordLabel, phoneLabel, emailLabel, editButton, deleteButton);
    }

    private Button createEditButton(int userID, String fullName, String password, String phone, String email) {
        Button button = new Button("Edit");
        button.setOnAction(event -> editUser(userID, fullName, password, phone, email));
        return button;
    }

    private Button createDeleteButton(int userID) {
        Button button = new Button("Delete");
        button.setOnAction(event -> deleteUser(userID));
        return button;
    }

    private void deleteUser(int userID) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String query = "DELETE FROM users WHERE userID = ?";
                try (Connection connectDB = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                    preparedStatement.setInt(1, userID);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        Platform.runLater(() -> reLoad());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    private void editUser(int userID, String fullname, String password, String phone, String email) {
        Platform.runLater(() -> {
            editPane.setVisible(true);
            editFullName.setText(fullname);
            editPassword.setText(password);
            editPhoneNumber.setText(phone);
            editEmail.setText(email);
        });

        confirmButton.setOnAction(event -> {
            String newFullName = editFullName.getText();
            String newPassword = editPassword.getText();
            String newPhone = editPhoneNumber.getText();
            String newEmail = editEmail.getText();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String query = "UPDATE users SET fullName = ?, password = ?, phoneNumber = ?, email = ? WHERE userID = ?";
                    try (Connection connectDB = DatabaseConnection.getConnection();
                         PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                        preparedStatement.setString(1, newFullName);
                        preparedStatement.setString(2, newPassword);
                        preparedStatement.setString(3, newPhone);
                        preparedStatement.setString(4, newEmail);
                        preparedStatement.setInt(5, userID);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            Platform.runLater(() -> {
                                editPane.setVisible(false);
                                reLoad(); // Gọi lại reLoad để cập nhật giao diện.
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });
    }
}
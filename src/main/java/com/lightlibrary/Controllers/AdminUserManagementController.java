package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    private Button cancelEditButton;

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

    @FXML
    private TextField addCoin;

    AdminDashboardController parentController;

    public AdminDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(AdminDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void autoUpdate() {
        reLoad();
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
        autoUpdate();
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
        Platform.runLater(() -> {
            // Create a confirmation alert
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this user?");
            confirmationAlert.setContentText("User ID: " + userID);

            // Show the alert and wait for a user response
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("OK") || response.getText().equals("Yes")) {
                    // Proceed with deletion
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            String query = "DELETE FROM users WHERE userID = ?";
                            try (Connection connectDB = DatabaseConnection.getConnection();
                                 PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

                                preparedStatement.setInt(1, userID);
                                int rowsAffected = preparedStatement.executeUpdate();

                                if (rowsAffected > 0) {
                                    Platform.runLater(() -> {
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                                        reLoad(); // Refresh the UI after deletion
                                    });
                                } else {
                                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the user."));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the user."));
                            }
                            return null;
                        }
                    };

                    new Thread(task).start();
                } else {
                    // Cancel deletion
                    showAlert(Alert.AlertType.INFORMATION, "Cancelled", "User deletion cancelled.");
                }
            });
        });
    }


    private void editUser(int userID, String fullName, String password, String phone, String email) {
        Platform.runLater(() -> {
            editPane.setVisible(true);
            editFullName.setText(fullName);
            editPassword.setText(password);
            editPhoneNumber.setText(phone);
            editEmail.setText(email);
            addCoin.setText("0");
        });

        confirmButton.setOnAction(event -> {
            String newFullName = editFullName.getText();
            String newPassword = editPassword.getText();
            String newPhone = editPhoneNumber.getText();
            String newEmail = editEmail.getText();
            String coinToAdd = addCoin.getText();

            // Validate input for coins
            double coinValue;
            try {
                coinValue = Double.parseDouble(coinToAdd);
                if (coinValue < 0.0D) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for coins.");
                return;
            }

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Connection connectDB = null;
                    PreparedStatement updateUserStatement = null;
                    PreparedStatement updateCoinsStatement = null;

                    try {
                        connectDB = DatabaseConnection.getConnection();

                        // Update user information
                        String updateUserQuery = "UPDATE users SET fullName = ?, password = ?, phoneNumber = ?, email = ? WHERE userID = ?";
                        updateUserStatement = connectDB.prepareStatement(updateUserQuery);
                        updateUserStatement.setString(1, newFullName);
                        updateUserStatement.setString(2, newPassword);
                        updateUserStatement.setString(3, newPhone);
                        updateUserStatement.setString(4, newEmail);
                        updateUserStatement.setInt(5, userID);

                        int rowsUpdated = updateUserStatement.executeUpdate();

                        // Add coins
                        String updateCoinsQuery = "UPDATE users SET coin = coin + ? WHERE userID = ?";
                        updateCoinsStatement = connectDB.prepareStatement(updateCoinsQuery);
                        updateCoinsStatement.setDouble(1, coinValue);
                        updateCoinsStatement.setInt(2, userID);

                        int coinsUpdated = updateCoinsStatement.executeUpdate();

                        if (rowsUpdated > 0 && coinsUpdated > 0) {
                            Platform.runLater(() -> {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated and coins added successfully.");
                                editPane.setVisible(false);
                                reLoad(); // Refresh UI
                            });
                        } else {
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user or add coins."));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the database."));
                    } finally {
                        if (updateUserStatement != null) updateUserStatement.close();
                        if (updateCoinsStatement != null) updateCoinsStatement.close();
                        if (connectDB != null) connectDB.close();
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });

        cancelEditButton.setOnAction(event -> {
            editPane.setVisible(false);
            editFullName.clear();
            editPassword.clear();
            editPhoneNumber.clear();
            editEmail.clear();
        });
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
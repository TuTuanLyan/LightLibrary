package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerSettingController implements Initializable {

    @FXML
    private AnchorPane rootSettingContainer;

    @FXML
    private Button changeAvatarButton;

    @FXML
    private ImageView avatarView;

    @FXML
    private Button saveAvatarButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private PasswordField confirmNewPasswordField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private TextField updateEmailField;

    @FXML
    private TextField updateNameTextField;

    @FXML
    private PasswordField updateNewPasswordField;

    @FXML
    private TextField updatePhoneNumberField;

    @FXML
    private Label userCoinLabel;

    @FXML
    private Label userIDLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private AnchorPane paymentPane;

    @FXML
    private TextField paymentContentField;

    private CustomerDashboardController customerDashboardController;

    public CustomerDashboardController getCustomerDashboardController() {
        return customerDashboardController;
    }

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
        Customer customer = customerDashboardController.getCustomer();
        userCoinLabel.setText(formatPrice(customer.getCoins()));
        userIDLabel.setText(String.format("#%08d", customer.getUserID()));
        userNameLabel.setText(customer.getFullName());
        emailLabel.setText(customer.getEmail() != null ? "Email: " + customer.getEmail() : "You have not set your email");
        phoneNumberLabel.setText(customer.getPhoneNumber() != null ? "Phone number: " + customer.getPhoneNumber() : "You have not set your phone number");

        changePasswordButton.setOnAction(event -> {
            updatePassword();
        });

        updateNameTextField.setOnAction(event -> {
            updateName();
        });

        updatePhoneNumberField.setOnAction(event -> {
            updatePhoneNumber();
        });

        updateEmailField.setOnAction(event -> {
            updateEmail();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentContentField.setEditable(false);
        // Gắn sự kiện cho nút "Đổi ảnh đại diện"
        changeAvatarButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh đại diện");

            // Lọc chỉ cho phép chọn file hình ảnh
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            // Mở trình duyệt file
            File selectedFile = fileChooser.showOpenDialog(getStage());
            if (selectedFile != null) {
                // Cập nhật và hiển thị ảnh mới trong ImageView
                Image avatar = new Image(selectedFile.toURI().toString());
                avatarView.setImage(avatar);

                // Nếu cần lưu đường dẫn vào cơ sở dữ liệu, bạn có thể thực hiện tại đây
                // Ví dụ: saveAvatarPathToDatabase(selectedFile.getAbsolutePath());
            }
        });
    }

    public void setPaymentPrompt() {
        String userID = String.format("#%08d ", customerDashboardController.getCustomer().getUserID());
        String name = customerDashboardController.getCustomer().getFullName();
        paymentContentField.setText(userID + name);
    }

    public void goBackToDashBoard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/lightlibrary/Views/CustomerDashboard.fxml"));
            Parent dashboard = loader.load();
            if (this.getCustomerDashboardController().getCustomer() != null) {
                CustomerDashboardController controller = loader.getController();
                controller.setCustomer(this.getCustomerDashboardController().getCustomer());
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Platform.runLater(stage::centerOnScreen);
            stage.setScene(new Scene(dashboard, 1440, 900));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Stage getStage() {
        return (Stage) rootSettingContainer.getScene().getWindow();
    }

    private String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        return numberFormat.format(price);
    }

    @FXML
    public void updatePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = updateNewPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();
        Customer customer = customerDashboardController.getCustomer();

        if (!customer.getPassword().equals(currentPassword)) {
            showAlert("Error", "Current password is incorrect.", Alert.AlertType.ERROR);
            return;
        }

        if (!customer.checkPasswordValidation(newPassword)) {
            showAlert("Error", "New password is not valid. It must contain at least 8 characters, a letter, and no consecutive numbers.", Alert.AlertType.ERROR);
            return;
        }

        if (currentPassword.equals(newPassword)) {
            showAlert("Error", "New password cannot be the same as the old password.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Confirm password does not match new password.", Alert.AlertType.ERROR);
            return;
        }

        // Cập nhật mật khẩu
        try {
            String sql = "UPDATE users SET password = ? WHERE userID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setInt(2, customer.getUserID());
                stmt.executeUpdate();
                customer.setPassword(newPassword);
                showAlert("Success", "Password updated successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to update password.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean updateField(String field, String value) {
        Customer customer = customerDashboardController.getCustomer();
        String sql = "UPDATE users SET " + field + " = ? WHERE userID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setInt(2, customer.getUserID());
            stmt.executeUpdate();
            showAlert("Success", "Updated " + field + " successfully.", Alert.AlertType.INFORMATION);
            return true;
        } catch (SQLException e) {
            showAlert("Error", "Failed to update " + field + ".", Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    public void updateName() {
        String newName = updateNameTextField.getText().trim();
        if (!customerDashboardController.getCustomer().checkNameValidation(newName)) {
            showAlert("Error", "Invalid name. Name must be at least 2 characters and contain only letters or spaces.", Alert.AlertType.ERROR);
            return;
        }
        customerDashboardController.getCustomer().setFullName(newName);
        if (updateField("fullName", newName)) {
            userNameLabel.setText(newName);
        }
    }

    public void updateEmail() {
        String newEmail = updateEmailField.getText().trim();
        if (!customerDashboardController.getCustomer().checkEmailValidation(newEmail)) {
            showAlert("Error", "Invalid email address. Please provide a valid email.", Alert.AlertType.ERROR);
            return;
        }
        customerDashboardController.getCustomer().setEmail(newEmail);
        if (updateField("email", newEmail)) {
            emailLabel.setText("Email: " + newEmail);
        }
    }

    public void updatePhoneNumber() {
        String newPhoneNumber = updatePhoneNumberField.getText().trim();
        if (!customerDashboardController.getCustomer().checkPhoneNumberValidation(newPhoneNumber)) {
            showAlert("Error", "Invalid phone number. It must contain only digits and be between 10-15 characters.", Alert.AlertType.ERROR);
            return;
        }
        customerDashboardController.getCustomer().setPhoneNumber(newPhoneNumber);
        if (updateField("phoneNumber", newPhoneNumber)) {
            phoneNumberLabel.setText("Phone number: " + newPhoneNumber);
        }
    }

    @FXML
    public void cancelPayment() {
        paymentPane.setVisible(false);
    }

    @FXML
    public void goToPayment(ActionEvent event) {
        setPaymentPrompt();
        paymentPane.setVisible(true);
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Platform.runLater(stage::centerOnScreen);
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}

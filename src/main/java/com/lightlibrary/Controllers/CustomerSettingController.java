package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    private PasswordField updateEmailField;

    @FXML
    private PasswordField updateNameTextField;

    @FXML
    private PasswordField updateNewPasswordField;

    @FXML
    private PasswordField updatePhoneNumberField;

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
        emailLabel.setText(customer.getEmail() != null ? customer.getEmail() : "You have not set your email");
        phoneNumberLabel.setText(customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "You have not set your phone number");
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

    public void updatePassword() {
        Customer customer = customerDashboardController.getCustomer();

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
}

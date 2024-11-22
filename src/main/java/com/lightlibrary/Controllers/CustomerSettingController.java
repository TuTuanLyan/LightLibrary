package com.lightlibrary.Controllers;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    private CustomerDashboardController customerDashboardController;

    public CustomerDashboardController getCustomerDashboardController() {
        return customerDashboardController;
    }

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    public void log() {
        // Thực hiện hành động khác nếu cần
    }
}

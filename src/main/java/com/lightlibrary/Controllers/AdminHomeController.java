package com.lightlibrary.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminHomeController implements Initializable, SyncAction {

    @FXML
    private AnchorPane homeRoot;

    @FXML
    private GridPane listBookGridPane;

    @FXML
    private GridPane listUserGridPane;

    @FXML
    private ImageView requiredBookImage;

    @FXML
    private ImageView totalBookImage;

    @FXML
    private ImageView totalUserImage;

    @FXML
    private ImageView transactionsImage;

    @FXML
    private Label totalBookLabel;

    @FXML
    private Label totalUserLabel;

    @FXML
    private Label transactionsLabel;

    @FXML
    private Label requiredBookLabel;

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

    }

    @Override
    public void setTheme(boolean darkMode) {
        homeRoot.getStylesheets().clear();
        if (darkMode) {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            totalBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-book-issue.png")).toExternalForm()));
            totalUserImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-user.png")).toExternalForm()));
            transactionsImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-transaction.png")).toExternalForm()));
            requiredBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-borrowed-book.png")).toExternalForm()));
        } else {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
            totalBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-book-issue.png")).toExternalForm()));
            totalUserImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-user.png")).toExternalForm()));
            transactionsImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-transaction.png")).toExternalForm()));
            requiredBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-borrowed-book.png")).toExternalForm()));
        }
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {}

    @FXML
    public void viewAndEditBook(ActionEvent event) {
        parentController.goToViewBookPage();;
    }

    @FXML
    public void viewAndEditUser(ActionEvent event) {
        parentController.goToUserManagementPage();
    }
}

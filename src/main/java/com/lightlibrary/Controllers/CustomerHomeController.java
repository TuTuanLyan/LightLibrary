package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerHomeController implements Initializable, SyncAction {

    @FXML
    private AnchorPane homeRoot;

    @FXML
    private Label curentTimeLabel;

    @FXML
    private Label customerWelcomeNameLabel;

    CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
        setWelcomeName(customerWelcomeNameLabel, parentController.getCustomer().getFullName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateDate(curentTimeLabel);
    }

    @Override
    public void setTheme(boolean darkMode) {
        homeRoot.getStylesheets().clear();
        if (darkMode) {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    private void updateDate(Label label) {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();

        // Định dạng
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH); // Thursday
        String formattedDate = today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));      // Jan 12, 2023

        // Kết quả cuối cùng
        label.setText(String.format("%s | %s", formattedDate, dayOfWeek));
    }

    private void setWelcomeName(Label customerWelcomeNameLabel, String welcomeName) {
        customerWelcomeNameLabel.setText(welcomeName);
        customerWelcomeNameLabel.setStyle("-fx-font-weight: bold;"
                + "-fx-text-fill: linear-gradient(to bottom right, #08d792, #7096ff);");
    }
}

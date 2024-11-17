package com.lightlibrary.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerSettingController {

    @FXML
    private AnchorPane rootSettingContainer;

    private CustomerDashboardController customerDashboardController;

    public CustomerDashboardController getCustomerDashboardController() {
        return customerDashboardController;
    }

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }

    public void goBackToDashBoard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/lightlibrary/Views/CustomerDashboard.fxml"));
            Parent dashboard = (Parent) loader.load();
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
}

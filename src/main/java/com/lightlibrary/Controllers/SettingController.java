package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SettingController {

    @FXML
    private AnchorPane rootSettingContainer;

    private CustomerDashboardController customerDashboardController;

    public CustomerDashboardController getCustomerDashboardController() {
        return customerDashboardController;
    }

    public void setCustomerDashboardController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }
}

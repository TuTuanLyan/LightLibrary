package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminHomeController implements Initializable, SyncAction {

    @FXML
    private AnchorPane homeRoot;

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
        } else {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {}


}

package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerHistoryController implements Initializable, SyncAction {

    @FXML
    private AnchorPane historyRoot;

    @FXML
    private DatePicker borrowDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField fillAuthorField;

    @FXML
    private TextField fillISBNField;

    @FXML
    private TextField fillTitleField;

    @FXML
    private TextField fillTotalField;

    @FXML
    private GridPane historyTableGrid;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Label spenrTotalCoinLabel;

    @FXML
    private GridPane titleGrid;

    CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public  void setParentController(AdminDashboardController parentController) {}

    @Override
    public void autoUpdate() {

    }

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setTheme(boolean darkMode) {
        historyRoot.getStylesheets().clear();
        if (darkMode) {
            historyRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            historyRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

}

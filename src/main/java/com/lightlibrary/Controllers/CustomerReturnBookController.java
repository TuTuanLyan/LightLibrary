package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerReturnBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane returnBookRoot;

    @FXML
    private TextField fillAuthorTextField;

    @FXML
    private DatePicker fillBorrowDatePicker;

    @FXML
    private DatePicker fillDueDatePicker;

    @FXML
    private TextField fillISBNTextField;

    @FXML
    private TextField fillTitleTextField;

    @FXML
    private GridPane notReturnBookTable;


    @FXML
    private CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public  void setParentController(AdminDashboardController parentController) {}

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setTheme(boolean darkMode) {
        returnBookRoot.getStylesheets().clear();
        if (darkMode) {
            returnBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            returnBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }
}

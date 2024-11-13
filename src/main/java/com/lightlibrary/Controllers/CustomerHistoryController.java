package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerHistoryController implements Initializable, SyncAction {

    @FXML
    private AnchorPane historyRoot;

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

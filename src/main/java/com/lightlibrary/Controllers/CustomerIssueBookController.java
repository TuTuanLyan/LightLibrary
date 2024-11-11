package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerIssueBookController implements Initializable, ThemeAction {

    @FXML
    private AnchorPane issueBookRoot;

    @FXML
    private Label searchResultLabel;

    @FXML
    private AnchorPane resultSearchContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void updateSearchResults(String query) {
        searchResultLabel.setText(query);
    }

    @Override
    public void setTheme(boolean darkMode) {
        issueBookRoot.getStylesheets().clear();
        if (darkMode) {
            issueBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
        } else {
            issueBookRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
        }
    }

}

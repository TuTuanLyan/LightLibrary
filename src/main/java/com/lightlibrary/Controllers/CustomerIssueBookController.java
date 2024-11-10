package com.lightlibrary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerIssueBookController implements Initializable {

    @FXML
    private Label searchResultLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void updateSearchResults(String result) {
        searchResultLabel.setText(result);
    }
}

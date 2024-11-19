package com.lightlibrary.Controllers;

import com.lightlibrary.Models.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.*;
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
        loadViewBook();
        loadViewUser();
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


    public void loadViewBook() {
        listBookGridPane.getChildren().clear();
        listBookGridPane.getRowConstraints().clear();

        Connection connection = DatabaseConnection.getConnection();
        String queryConnect = "SELECT * FROM books";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryConnect);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("TITLE");
                String author = resultSet.getString("AUTHOR");
                int available = resultSet.getInt("availableNumber");
                listBookGridPane.addRow(listBookGridPane.getRowCount(),new Label(ISBN),new Label(title),new Label(author),new Label(Integer.toString(available)));
                RowConstraints curConstraints = new RowConstraints();
                curConstraints.setMinHeight(70);
                listBookGridPane.getRowConstraints().add(curConstraints);
            }

        } catch (SQLException e) {
                throw new RuntimeException(e);
        }
    }

    public void loadViewUser() {
        listUserGridPane.getChildren().clear();
        listUserGridPane.getRowConstraints().clear();

        Connection connection = DatabaseConnection.getConnection();
        String queryConnect = "select " +
                "u.*," +
                "case " +
                "when " +
                "count(t.userID) > 0 then count(t.userID) " +
                "else 0 " +
                "end as totalBorrowed " +
                "from users u left join transactions t  on u.userID = t.userID " +
                "group by u.userID " +
                "order by u.userID;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryConnect);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String fullName = resultSet.getString("fullName");
                String userName = resultSet.getString("userName");
                int totalBorrowed = resultSet.getInt("totalBorrowed");

                listUserGridPane.addRow(listUserGridPane.getRowCount(),new Label(Integer.toString(userID)),new Label(userName),new Label(fullName),new Label(Integer.toString(totalBorrowed)));
                RowConstraints curConstraints = new RowConstraints();
                curConstraints.setMinHeight(70);
                listUserGridPane.getRowConstraints().add(curConstraints);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @FXML
    private ImageView borrowedBookImage;

    @FXML
    private ImageView overdueBookImage;

    @FXML
    private ImageView returnedBookImage;

    @FXML
    private Label borrowedBookAmountLabel;

    @FXML
    private Label overdueBookAmountLabel;

    @FXML
    private Label returnedBookAmountLabel;

    @FXML
    private GridPane borrowingGrid;

    CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    @Override
    public  void setParentController(AdminDashboardController parentController) {}

    @Override
    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
        setWelcomeName(customerWelcomeNameLabel, parentController.getCustomer().getFullName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateDate(curentTimeLabel);
        System.out.println(parentController.toString());
        disPlayTopChoices();
    }

    @Override
    public void setTheme(boolean darkMode) {
        homeRoot.getStylesheets().clear();
        if (darkMode) {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            borrowedBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-borrowed-book.png")).toExternalForm()));
            overdueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-overdue.png")).toExternalForm()));
            returnedBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-return.png")).toExternalForm()));
        } else {
            homeRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());

            borrowedBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-borrowed-book.png")).toExternalForm()));
            overdueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-overdue.png")).toExternalForm()));
            returnedBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-return.png")).toExternalForm()));
        }
    }

    private void updateDate(Label label) {
        LocalDate today = LocalDate.now();

        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String formattedDate = today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        label.setText(String.format("%s | %s", formattedDate, dayOfWeek));
    }

    private void setWelcomeName(Label customerWelcomeNameLabel, String welcomeName) {
        customerWelcomeNameLabel.setText(welcomeName);
        customerWelcomeNameLabel.setStyle("-fx-font-weight: bold;"
                + "-fx-text-fill: linear-gradient(to bottom right, #08d792, #7096ff);");
    }

    public void viewHistoryTransaction(ActionEvent actionEvent) {
        parentController.goToHistoryPage();
    }

    public void disPlayTopChoices() {
        Task<Void> loadTopBooksTask = new Task<>() {

            @Override
            protected Void call() throws SQLException {
                Connection connectDB = DatabaseConnection.getConnection();

                if (connectDB == null) {
                    throw new SQLException("Connection is null");
                }

                String query = "SELECT b.title, b.author, b.thumbnail \n" +
                        "FROM transactions t\n" +
                        "JOIN books b ON t.isbn = b.isbn\n" +
                        "GROUP BY t.isbn\n" +
                        "ORDER BY COUNT(b.title and t.isbn) DESC\n" +
                        "LIMIT 8;";

                try (PreparedStatement preparedStatement = connectDB.prepareStatement(query);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
                    int index = 0;
                    while (resultSet.next()) {
                        if (index > 8) break;

                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        String thumbnailUrl = resultSet.getString("thumbnail");

                        // Create UI components for each book
                        Pane bookBox = ControllerUtil.createTopBookBlock(thumbnailUrl, title, author);
                        bookBox.setLayoutX(index * 160 + 20);
                        bookBox.setLayoutY(585);

                        // Update the UI on the JavaFX Application Thread
                        Platform.runLater(() -> homeRoot.getChildren().add(bookBox));

                        index++;
                    }
                }
                return null;
            }
        };

        loadTopBooksTask.setOnFailed(event -> {
            System.err.println("Failed to load top books: " + loadTopBooksTask.getException().getMessage());
        });

        Thread thread = new Thread(loadTopBooksTask);
        thread.setDaemon(true); // Ensure thread exits when the application closes
        thread.start();
    }


    public void loadBorrowingGrid() {
        borrowingGrid.getChildren().clear();
        borrowingGrid.getRowConstraints().clear();

        int userID = parentController.getCustomer().getUserID();
        System.out.println(userID);
        Connection connectDB = DatabaseConnection.getConnection();
        String queryBorrowing = "select b.isbn,b.title,b.author,t.dueDate from transactions t left join books b on b.isbn = t.isbn where t.userID=?";
        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(queryBorrowing);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String dueDate = resultSet.getDate("dueDate").toLocalDate().toString();

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setMinHeight(70);
                borrowingGrid.getRowConstraints().add(rowConstraints);
                borrowingGrid.addRow(borrowingGrid.getRowCount(),new Label(isbn),new Label(title),new Label(author),new Label(dueDate));

            }
        } catch (SQLException e) {
            System.err.println("cannot send queryBorrowing in Customer");
        }

    }
}

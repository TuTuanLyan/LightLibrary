package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminIssueBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane issueBookRoot;

    @FXML
    private AnchorPane resultSearchContainer;

    @FXML
    private Label detailDescriptionLabel;

    @FXML
    private Label detailPublisherLabel;

    @FXML
    private Label detailPublishDateLabel;

    @FXML
    private Label detailISBNLabel;

    @FXML
    private Label detailAuthorLabel;

    @FXML
    private Label detailTitleLabel;

    @FXML
    private ImageView detailThumbnailImage;

    @FXML
    private TextField numbersOfAddBookTextField;

    @FXML
    private TextField feePerDayTextField;

    @FXML
    private Button addBookButton;

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
        addBookButton.setOnAction(this::addBookAction);
        addBookButton.setDisable(true);
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

    @Override
    public void setParentController(CustomerDashboardController parentController) {}

    public void updateSearchResults(String query) {
        if (query.isEmpty()) return;

        resultSearchContainer.setVisible(true);
        resultSearchContainer.getChildren().clear();

        if ((query.contains("nguoi yeu") || query.contains("ny")) && query.contains("kieu van tuyen")) {
            Pane notFound = createNotFound();
            resultSearchContainer.getChildren().add(notFound);
            resultSearchContainer.setPrefHeight(500);
            return;
        }

        Pane loadingPane = createLoading("Searching, please wait...");
        resultSearchContainer.getChildren().add(loadingPane);
        resultSearchContainer.setPrefHeight(500);

        Task<List<Volume>> searchTask = new Task<List<Volume>>() {
            @Override
            protected List<Volume> call() throws Exception {
                return GoogleBooksAPIClient.searchBooks(query);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Volume> volumes = searchTask.getValue();
            resultSearchContainer.getChildren().clear();

            if (volumes == null || volumes.size() <= 2) {
                Pane notFound = createNotFound();
                resultSearchContainer.getChildren().add(notFound);
                resultSearchContainer.setPrefHeight(500);
                return;
            }

            int index = 0;
            for (Volume volume : volumes) {
                Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

                String thumbnailURL = volumeInfo.getImageLinks() != null ?
                        volumeInfo.getImageLinks().getThumbnail() : "https://birkhauser.com/product-not-found.png";
                String title = volumeInfo.getTitle();
                String authors = volumeInfo.getAuthors() != null ?
                        String.join(", ", volumeInfo.getAuthors()) : "Unknown Author";
                String ISBN = GoogleBooksAPIClient.getISBN(volumeInfo);
                String description = volumeInfo.getDescription() != null ?
                        volumeInfo.getDescription() : "There is no description for this book :(";
                String publisher = volumeInfo.getPublisher() != null ?
                        volumeInfo.getPublisher() : "Unknown Publisher";
                String publishedDate = volumeInfo.getPublishedDate() != null ?
                        volumeInfo.getPublishedDate() : "Unknown published date";


                int blockIndex = index;
                Task<Pane> blockTask = new Task<Pane>() {
                    @Override
                    protected Pane call() throws Exception {
                        Pane bookPane = ControllerUtil.createBookBlock(thumbnailURL, title, authors, ISBN, description);
                        Button viewDetailButton = createViewDetailButton();
                        viewDetailButton.setOnAction(e -> {
                            showBookDetail(thumbnailURL, title, authors, ISBN,
                                    description, publisher, publishedDate);
                        });
                        bookPane.getChildren().add(viewDetailButton);
                        bookPane.setPrefSize(580, 265);
                        bookPane.setLayoutX(15);
                        bookPane.setLayoutY(25 + blockIndex * 280);
                        return bookPane;
                    }
                };

                blockTask.setOnSucceeded(event -> {
                    Pane resultBlock = blockTask.getValue();
                    Platform.runLater(() -> resultSearchContainer.getChildren().add(resultBlock));
                });

                blockTask.setOnFailed(blockWorker -> {
                    System.out.println("Error occurred during create result block where index = " + blockIndex);
                    System.out.println(searchTask.getException().getMessage());
                });

                Thread blockThread = new Thread(blockTask);
                blockThread.setDaemon(true);
                blockThread.start();

                index++;
            }

            resultSearchContainer.setPrefHeight(index * 280 + 30);
        });

        searchTask.setOnFailed(e -> {
            resultSearchContainer.getChildren().clear();
            Pane notFound = createNotFound();
            resultSearchContainer.getChildren().add(notFound);
        });

        new Thread(searchTask).start();
    }

    private Pane createNotFound() {
        ImageView notFoundImage = new ImageView(new Image(String.valueOf(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Images/not-found.png")))));
        notFoundImage.setFitHeight(360);
        notFoundImage.setFitWidth(480);
        notFoundImage.setLayoutX(0);
        notFoundImage.setLayoutY(0);

        Label notFoundLabel = new Label("Even the AI could not find anything like that :(");
        notFoundLabel.setPrefSize(480, 110);
        notFoundLabel.setWrapText(true);
        notFoundLabel.setLayoutX(0);
        notFoundLabel.setLayoutY(360);
        notFoundLabel.setStyle("-fx-font-weight: bold;" + "-fx-alignment: center;"
                + "-fx-text-alignment: center;" + "-fx-font-size: 36px;"
                + "-fx-text-fill: linear-gradient(to bottom right, #08d792, #7096ff);");

        Pane notFoundPane = new Pane(notFoundImage, notFoundLabel);
        notFoundPane.setPrefSize(480, 480);
        notFoundPane.setLayoutX(65);
        notFoundPane.setLayoutY(85);

        return notFoundPane;
    }

    private Pane createLoading(String loadingText) {
        ImageView loading = new ImageView(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/com/lightlibrary/Images/loadingGif.gif"))));
        loading.setFitHeight(300);
        loading.setFitWidth(300);
        loading.setLayoutX(80);
        loading.setLayoutY(0);

        Label loadingLabel = new Label(loadingText);
        loadingLabel.setPrefSize(460, 60);
        loadingLabel.setLayoutX(0);
        loadingLabel.setLayoutY(300);
        loadingLabel.setStyle("-fx-font-weight: bold;" + "-fx-alignment: center;"
                + "-fx-text-fill: linear-gradient(to bottom right, #08d792, #7096ff);");

        Pane loadingPane = new Pane(loading, loadingLabel);
        loadingPane.setPrefSize(460, 415);
        loadingPane.setLayoutX(75);
        loadingPane.setLayoutY(145);

        return loadingPane;
    }

    private Button createViewDetailButton() {
        Button viewDetailButton = new Button("View Detail");
        viewDetailButton.setPrefSize(120, 40);
        viewDetailButton.setLayoutX(450);
        viewDetailButton.setLayoutY(180);
        viewDetailButton.getStyleClass().add("view-detail-button");

        return viewDetailButton;
    }

    public void showBookDetail(String thumbnailUrl,String title, String author,String ISBN,
                               String description, String publisher, String publishDate) {
        addBookButton.setDisable(false);
        numbersOfAddBookTextField.clear();
        feePerDayTextField.clear();

        detailThumbnailImage.setImage(new Image(thumbnailUrl));
        detailAuthorLabel.setText(author);
        detailTitleLabel.setText(title);
        detailDescriptionLabel.setText(description);
        detailISBNLabel.setText(ISBN);
        detailPublisherLabel.setText(publisher);
        detailPublishDateLabel.setText(publishDate);
    }

    public void addBookAction(ActionEvent event) {
        // Lấy dữ liệu từ các trường nhập liệu
        String numberOfBooksStr = numbersOfAddBookTextField.getText();
        String feePerDayStr = feePerDayTextField.getText();
        String title = detailTitleLabel.getText();
        String author = detailAuthorLabel.getText();
        String publisher = detailPublisherLabel.getText();
        String publishedDate = detailPublishDateLabel.getText();
        String isbn = detailISBNLabel.getText();
        String description = detailDescriptionLabel.getText().length() > 999 ?
                detailDescriptionLabel.getText().substring(0, 999) : detailDescriptionLabel.getText();
        String thumbnail = detailThumbnailImage.getImage() != null ?
                detailThumbnailImage.getImage().getUrl() : null;

        // Kiểm tra thông tin cơ bản
        if (title.isEmpty() || isbn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Title and ISBN cannot be empty.");
            return;
        }

        try {
            int numberOfBooks = Integer.parseInt(numberOfBooksStr);
            double feePerDay = Double.parseDouble(feePerDayStr);

            if (numberOfBooks < 1 || feePerDay < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "The number of books must be at least 1 and the fee cannot be negative.");
                return;
            }

            // Kết nối và xử lý cơ sở dữ liệu
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Kiểm tra sách đã tồn tại hay chưa
                String checkQuery = "SELECT COUNT(*) FROM books WHERE isbn = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, isbn);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        showAlert(Alert.AlertType.ERROR, "Duplicate Error", "This book already exists in the database.");
                        return;
                    }
                }

                // Thêm sách vào cơ sở dữ liệu
                String insertQuery = "INSERT INTO books (totalNumber, availableNumber, title, author, publisher, " +
                        "publishedDate, isbn, thumbnail, description, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, numberOfBooks);
                    insertStmt.setInt(2, numberOfBooks); // Ban đầu số sách còn lại = tổng số sách
                    insertStmt.setString(3, title);
                    insertStmt.setString(4, author.isEmpty() ? "null" : author);
                    insertStmt.setString(5, publisher);
                    insertStmt.setString(6, publishedDate);
                    insertStmt.setString(7, isbn);
                    insertStmt.setString(8, thumbnail);
                    insertStmt.setString(9, description);
                    insertStmt.setDouble(10, feePerDay);

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add book to the database.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid number or fee value.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while processing the database.");
        }
    }

    @FXML
    public void cancelViewDetail(ActionEvent event) {
        detailThumbnailImage.setImage(new Image(Objects
                .requireNonNull(Objects.requireNonNull(getClass()
                        .getResource("/com/lightlibrary/Images/LightLibraryLogo.png")).toExternalForm())));
        detailAuthorLabel.setText("Author");
        detailTitleLabel.setText("Title");
        detailDescriptionLabel.setText("Description");
        detailISBNLabel.setText("ISBN");
        detailPublisherLabel.setText("Publisher");
        detailPublishDateLabel.setText("Published Date");

        numbersOfAddBookTextField.clear();
        feePerDayTextField.clear();

        addBookButton.setDisable(true);
    }

    /**
     * Hiển thị thông báo cho người dùng.
     *
     * @param alertType Loại thông báo (ERROR, INFORMATION, etc.)
     * @param title     Tiêu đề thông báo
     * @param message   Nội dung thông báo
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

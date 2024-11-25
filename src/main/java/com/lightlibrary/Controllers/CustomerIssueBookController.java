package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerIssueBookController implements Initializable, SyncAction {

    @FXML
    private AnchorPane issueBookRoot;

    @FXML
    private AnchorPane resultSearchContainer;

    @FXML
    private Pane detailBookPane;

    @FXML
    private Button addToFavouriteListButton;

    @FXML
    private Button borrowBookButton;

    @FXML
    private Button requireBookButton;

    @FXML
    private Button detailCloseButton;

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
    private Label detailPriceLabel;

    @FXML
    private Label detailTItleLabel;

    @FXML
    private ImageView detailThumbnailImage;

    @FXML
    private DatePicker pickDueDatePiker;

    @FXML
    private TextField borrowDaysAmount;

    @FXML
    private  AnchorPane confirmBorrowPane;

    @FXML
    private Label confirmFeePerDay;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button cancleBorrowButton;

    @FXML
    private Button confirmBorrowButton;

    @FXML
    private GridPane favouriteBookTable;

    private CustomerDashboardController parentController;

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
        loadingFavor();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        detailCloseButton.setOnAction(e -> {
            addToFavouriteListButton.setVisible(false);
            borrowBookButton.setVisible(false);
            requireBookButton.setVisible(false);
            detailThumbnailImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/LightLibraryLogo.png")).toExternalForm()));
            detailPriceLabel.setText("Fee / Day");
            detailTItleLabel.setText("Title");
            detailDescriptionLabel.setText("Description");
            detailPublisherLabel.setText("Publisher");
            detailPublishDateLabel.setText("Publish Date");
            detailISBNLabel.setText("ISBN");
            detailAuthorLabel.setText("Author");
        });

        pickDueDatePiker.setOnAction(event -> handleDueDatePickerAction());
        borrowDaysAmount.setOnAction(event -> setupOnActionForBorrowDays());

        confirmBorrowPane.setVisible(false);
        cancleBorrowButton.setOnAction(e -> {
            confirmBorrowPane.setVisible(false);
            pickDueDatePiker.setValue(LocalDate.now().plusDays(1));
            borrowDaysAmount.clear();
            confirmFeePerDay.setText("Fee / Days: ");
            totalPriceLabel.setText("Total Price: ");
        });
    }

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

    public void showBookDetail(String thumbnailUrl,String title, String author,String ISBN,
                               String description, String publisher, String publishDate) {

        detailBookPane.setVisible(true);
        Task<Double> checkISBNTask = new Task<>() {
            @Override
            protected Double call() throws Exception {

                Platform.runLater(() -> {
                    detailThumbnailImage.setImage(new Image(thumbnailUrl));
                    detailAuthorLabel.setText(author);
                    detailTItleLabel.setText(title);
                    detailDescriptionLabel.setText(description);
                    detailISBNLabel.setText(ISBN);
                    detailPublisherLabel.setText(publisher);
                    detailPublishDateLabel.setText(publishDate);
                });

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT availableNumber, price FROM books WHERE isbn = ?")) {
                    preparedStatement.setString(1, ISBN);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int availableNumber = resultSet.getInt("availableNumber");
                            double price = resultSet.getDouble("price");
                            if (availableNumber > 0) {
                                return price;
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 0.0;
            }
        };

        checkISBNTask.setOnSucceeded(event -> {
            double price = checkISBNTask.getValue();
            detailBookPane.setVisible(true);

            if (price > 0.0D) {
                addToFavouriteListButton.setVisible(true);
                addToFavouriteListButton.setOnAction(e -> {
                    Customer customer = parentController.getCustomer();
                    addFavoriteBook(customer.getUserID(), ISBN,title,author);
                });
                borrowBookButton.setVisible(true);
                borrowBookButton.setOnAction(e -> {
                    confirmBorrowPane.setVisible(true);
                    confirmFeePerDay.setText("Fee / Day: " + formatPrice(price));

                    confirmBorrowButton.setOnAction(confirmEvent -> {
                        setupOnActionForBorrowDays();
                        LocalDate dueDate = pickDueDatePiker.getValue();
                        if (dueDate == null) {
                            showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a due date!");
                            return;
                        }

                        double totalPrice = getTotalPrice(Integer.parseInt(borrowDaysAmount.getText() != null ?
                                borrowDaysAmount.getText() : "0"));
                        borrowBook(parentController.getCustomer(), ISBN, totalPrice);
                        confirmBorrowPane.setVisible(false);
                        pickDueDatePiker.setValue(LocalDate.now().plusDays(1));
                        borrowDaysAmount.clear();
                        confirmFeePerDay.setText("Fee / Days: ");
                        totalPriceLabel.setText("Total Price: ");
                    });
                });
                requireBookButton.setVisible(false);

                detailPriceLabel.setText("Fee / Day:\n" + price);
            } else {
                addToFavouriteListButton.setVisible(false);
                borrowBookButton.setVisible(false);
                requireBookButton.setVisible(true);
                requireBookButton.setOnAction(e -> {
                    Customer customer = parentController.getCustomer();
                    addRequiredBook(customer.getUserID(), ISBN, title);
                });

                detailPriceLabel.setText("This book is not available");
            }
        });

        checkISBNTask.setOnFailed(event -> {
            System.out.println("Error checking ISBN in database.");
        });

        Thread checkISBNThread = new Thread(checkISBNTask);
        checkISBNThread.setDaemon(true);
        checkISBNThread.start();
    }

    private Button createViewDetailButton() {
        Button viewDetailButton = new Button("View Detail");
        viewDetailButton.setPrefSize(120, 40);
        viewDetailButton.setLayoutX(450);
        viewDetailButton.setLayoutY(180);
        viewDetailButton.getStyleClass().add("view-detail-button");

        return viewDetailButton;
    }

    private void setupOnActionForBorrowDays() {
        String input = borrowDaysAmount.getText().trim();

        if (input.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "Invalid number of days entered!");
            return;
        }

        try {
            int daysToExtend = Integer.parseInt(input);

            if (daysToExtend < 0) {
                borrowDaysAmount.setText("0");
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "The renewal date cannot be negative!");
            } else {
                LocalDate newDueDate = LocalDate.now().plusDays(daysToExtend);
                pickDueDatePiker.setValue(newDueDate);
                updateTotalPrice(daysToExtend);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "Invalid number of days entered!");
        }
    }

    private void handleDueDatePickerAction() {
        LocalDate selectedDate = pickDueDatePiker.getValue();

        if (selectedDate == null && borrowDaysAmount.getText() == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "The selected date is invalid!");
            return;
        }

        long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), selectedDate);

        if (daysBetween < 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "The renewal date cannot be before the current date.");
            pickDueDatePiker.setValue(LocalDate.now());
            borrowDaysAmount.setText("0");
            updateTotalPrice(0);
        } else {
            borrowDaysAmount.setText(String.valueOf(daysBetween));
            updateTotalPrice((int) daysBetween);
        }
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

    private double getTotalPrice(int days) {
        String detailPriceText = detailPriceLabel.getText().trim();

        String[] lines = detailPriceText.split("\n");
        String priceText = lines[lines.length - 1].trim();

        double pricePerDay = Double.parseDouble(priceText);
        return pricePerDay * days - (int)(days / 15)  * 0.02 * pricePerDay;
    }

    private void updateTotalPrice(int days) {
        try {
            double totalPrice = getTotalPrice(days);
            totalPriceLabel.setText("Total Price: " + formatPrice(totalPrice));
        } catch (NumberFormatException e) {
            totalPriceLabel.setText("0.00");
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price format in detailPriceLabel!");
        } catch (ArrayIndexOutOfBoundsException e) {
            totalPriceLabel.setText("0.00");
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid detailPriceLabel format!");
        }
    }

    private String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        return numberFormat.format(price);
    }

    private void borrowBook(Customer customer, String ISBN, double totalPrice) {
        if (customer.getCoins() < totalPrice) {
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Insufficient Funds",
                            "You do not have enough coins to borrow this book."));
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Bắt đầu transaction

            // Kiểm tra xem người dùng đã mượn cuốn sách này và chưa trả
            String checkTransactionQuery = "SELECT * FROM transactions WHERE userID = ? AND isbn = ? AND returnDate IS NULL";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkTransactionQuery)) {
                checkStatement.setInt(1, customer.getUserID());
                checkStatement.setString(2, ISBN);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Nếu đã mượn và chưa trả, không thể mượn lại
                        showAlert(Alert.AlertType.ERROR, "Book Already Borrowed", "You have already borrowed this book and have not returned it.");
                        return;
                    }
                }
            }

            // Giảm số lượng sách
            String updateBookQuery = "UPDATE books SET availableNumber = availableNumber - 1 WHERE isbn = ?";
            try (PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {
                updateBookStmt.setString(1, ISBN);
                int rowsAffected = updateBookStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to update book availability.");
                }
            }

            // Ghi vào bảng transactions
            String insertTransactionQuery = "INSERT INTO transactions (userID, isbn, borrowDate, dueDate, borrowFee, totalPrice) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                insertTransactionStmt.setInt(1, customer.getUserID());
                insertTransactionStmt.setString(2, ISBN);
                insertTransactionStmt.setDate(3, Date.valueOf(LocalDate.now()));
                insertTransactionStmt.setDate(4, Date.valueOf(pickDueDatePiker.getValue()));
                insertTransactionStmt.setDouble(5, totalPrice);
                insertTransactionStmt.setDouble(6, totalPrice);
                insertTransactionStmt.executeUpdate();
            }

            // Trừ coin người dùng
            double updatedCoin = customer.getCoins() - totalPrice;
            customer.setCoins(updatedCoin); // Cập nhật thông tin trên đối tượng
            parentController.updateCoin(updatedCoin);
            String updateUserQuery = "UPDATE users SET coin = ? WHERE userID = ?";
            try (PreparedStatement updateUserStmt = connection.prepareStatement(updateUserQuery)) {
                updateUserStmt.setDouble(1, updatedCoin);
                updateUserStmt.setInt(2, customer.getUserID());
                updateUserStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while borrowing the book."));
        }
    }

    public void addRequiredBook(int userID, String ISBN, String title) {
        String checkSql = "SELECT * FROM requiredBooks WHERE userID = ? AND ISBN = ?";
        String insertSql = "INSERT INTO requiredBooks (userID, ISBN, title) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

            // Check if the required book already exists for the user
            checkStatement.setInt(1, userID);
            checkStatement.setString(2, ISBN);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Entry", "This book is already in your requiredBooks list and unresolved.");
                return;
            }

            // Add the required book
            insertStatement.setInt(1, userID);
            insertStatement.setString(2, ISBN);
            insertStatement.setString(3, title);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added to requiredBooks successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No rows were affected. Please try again.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding book to requiredBooks: " + e.getMessage());
        }
    }

    public void addFavoriteBook(int userID, String ISBN, String title,String author) {
        String checkSql = "SELECT * FROM favoriteBooks WHERE userID = ? AND ISBN = ?";
        String insertSql = "INSERT INTO favoriteBooks (userID, ISBN) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

            // Check if the book is already in the favorite list
            checkStatement.setInt(1, userID);
            checkStatement.setString(2, ISBN);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Entry", "This book is already in your favoriteBooks list.");
                return;
            }

            // Add the book to favoriteBooks
            insertStatement.setInt(1, userID);
            insertStatement.setString(2, ISBN);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added to favoriteBooks successfully!");
                loadingFavor();
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No rows were affected. Please try again.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding book to favoriteBooks: " + e.getMessage());
        }
    }

    public void loadingFavor() {
        Connection connection = DatabaseConnection.getConnection();
        String loadingQuery = "SELECT b.isbn, b.title, b.author FROM favoriteBooks f JOIN books b ON f.isbn = b.isbn WHERE f.userID = ?";
        Platform.runLater(this::clearRow);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(loadingQuery);
            preparedStatement.setInt(1,parentController.getCustomer().getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Platform.runLater(() -> addRow(isbn,title,author));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading favoriteBooks");
        }

    }

    private void addRow(String isbn, String title, String author) {
        Label isbnLabel = new Label(isbn);
        Label titleLabel = new Label(title);
        Label authorLabel = new Label(author);
        Button removeButton = createRemoveButton(parentController.getCustomer().getUserID(),isbn);
        favouriteBookTable.addRow(favouriteBookTable.getRowCount(), isbnLabel, titleLabel, authorLabel, removeButton);
        favouriteBookTable.getRowConstraints().add(new RowConstraints(70));
    }
    private void clearRow() {
        favouriteBookTable.getRowConstraints().clear();
        favouriteBookTable.getChildren().clear();
    }

    private Button createRemoveButton(int userID,String isbn) {
        Button button = new Button("Remove");
        button.setOnAction(event -> {
            removeFavor(userID,isbn);
            loadingFavor();
        });
        return button;
    }

    private void removeFavor(int userID,String isbn) {
        Connection connection = DatabaseConnection.getConnection();
        String query = "DELETE FROM favoriteBooks WHERE userID = ? AND ISBN = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, isbn);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book removed from FavouriteBooks successfully!");
            }
            else {
                showAlert(Alert.AlertType.WARNING, "Warning", "This book not be removed from FavouriteBooks successfully! Please try again.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error deleting favoriteBooks have isbn: " + isbn);
        }
    }
}

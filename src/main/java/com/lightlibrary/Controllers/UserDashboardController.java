package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML
    private Pane avatarContainer;

    @FXML
    private ImageView avatarImage;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private AnchorPane individualButtonContainer;
    private static boolean individualBarShown = false;

    @FXML
    private Button dashboardButton;

    @FXML
    private AnchorPane navigationButtonBorder;
    @FXML
    private Pane subButtonBorder;

    @FXML
    private Button issueBookButton;

    @FXML
    private Button returnBookButton;

    @FXML
    private Button supportButton;

    @FXML
    private Pane dashboardContent;
    @FXML
    private AnchorPane topBookDisplayBox;

    @FXML
    private Pane issueBookContent;

    @FXML
    private Pane returnBookContent;

    @FXML
    private Pane historyContent;

    @FXML
    private Circle searchLoadingDot1;

    @FXML
    private Circle searchLoadingDot2;

    @FXML
    private Circle searchLoadingDot3;

    @FXML
    private Label customerFullNameField;

    @FXML
    private Label customerUserIDField;

    @FXML
    private Pane searchingAnimationPane;

    @FXML
    private ScrollPane resultAPISearchScrollPane;

    /**
     * Enum representing the active navigation button on the dashboard.
     */
    enum ActiveButton {
        DASHBOARD,
        ISSUE_BOOK,
        RETURN_BOOK,
        HISTORY
    }

    private static ActiveButton activeButton;

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        displayCustomerInformation();
    }

    /**
     * Initializes the User Dashboard controller, setting up event handlers and UI elements.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set view individual bar.
        individualButtonContainer.setVisible(false);
        Circle avatarClip = new Circle(28, 28, 28);
        avatarContainer.setClip(avatarClip);

        // Set animation and handle action navigation bar.
        handleNavigationBarAction();

        // Display Top book when Dashboard initial.
        displayTopBook(topBookDisplayBox);

        // Search Action.
        searchBar.setOnAction(event -> {
            AnchorPane resultSearchBar = (AnchorPane) resultAPISearchScrollPane.getContent();
            displayResultSearch(resultSearchBar, searchBar.getText());
        });

        searchButton.setOnAction(event -> {
            AnchorPane resultSearchBar = (AnchorPane) resultAPISearchScrollPane.getContent();
            displayResultSearch(resultSearchBar, searchBar.getText());
        });
    }

    /**
     * Handles the visibility of the individual button bar and animates its sliding action.
     *
     * @param event the ActionEvent triggered by clicking the button
     */
    @FXML
    protected void handleIndividualBarAction(ActionEvent event) {
        TranslateTransition individualBarTransition = new TranslateTransition();
        individualBarTransition.setNode(individualButtonContainer);
        individualBarTransition.setDuration(Duration.seconds(0.35));

        if (!individualBarShown) {
            individualButtonContainer.setVisible(true);
            individualBarTransition.setToY(110);
            individualBarShown = true;
        } else {
            individualBarTransition.setToY(-35);
            individualBarTransition.setOnFinished(e -> {
                individualButtonContainer.setVisible(false);
                individualBarShown = false;
            });
        }

        individualBarTransition.play();
    }

    /**
     * Handles the movement of the navigation button border and updates the selected button's style.
     *
     * @param activeButton the button that was clicked and is now active
     */
    private void handleNavigationButtonBorder(Button activeButton) {
        TranslateTransition navigationBarTransition = new TranslateTransition();
        navigationBarTransition.setNode(navigationButtonBorder);
        navigationBarTransition.setDuration(Duration.seconds(0.2));
        navigationBarTransition.setToY(activeButton.getLayoutY() - navigationButtonBorder.getLayoutY());
        navigationBarTransition.play();

        TranslateTransition subButtonBorderTransition = new TranslateTransition();
        subButtonBorderTransition.setNode(subButtonBorder);
        subButtonBorderTransition.setDuration(Duration.seconds(0.2));
        subButtonBorderTransition.setToY(activeButton.getLayoutY() - subButtonBorder.getLayoutY());
        subButtonBorderTransition.play();

        dashboardButton.getStyleClass().remove("selected");
        issueBookButton.getStyleClass().remove("selected");
        returnBookButton.getStyleClass().remove("selected");
        supportButton.getStyleClass().remove("selected");

        activeButton.getStyleClass().add("selected");
    }

    /**
     * Logs the user out and switches the scene back to the Login and Register view.
     *
     * @param event the ActionEvent triggered by clicking the logout button
     * @throws IOException if the Login and Register FXML file cannot be loaded
     */
    @FXML
    protected void handleLogoutAction(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }

    /**
     * Handle animation in main content when swapped.
     * @param newContent is the new content Pane user want to go.
     */
    private void swapMainContentAnimation(Pane newContent) {
        Pane currentContent = switch (activeButton) {
            case DASHBOARD -> dashboardContent;
            case ISSUE_BOOK -> issueBookContent;
            case RETURN_BOOK -> returnBookContent;
            case HISTORY -> historyContent;
        };

        if (currentContent == newContent) {
            return;
        }

        FadeTransition currentContentTransition = new FadeTransition(Duration.seconds(0.3), currentContent);
        currentContentTransition.setFromValue(1.0);
        currentContentTransition.setToValue(0.0);
        currentContentTransition.play();
        currentContentTransition.setOnFinished(e -> {
           currentContent.setVisible(false);
        });

        newContent.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), newContent);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    /**
     * Create animation loading when do long time task.
     */
    private void loadingAction() {
        SequentialTransition dot1Animation = createDotAnimation(searchLoadingDot1, 0);
        SequentialTransition dot2Animation = createDotAnimation(searchLoadingDot2, 250);
        SequentialTransition dot3Animation = createDotAnimation(searchLoadingDot3, 500);

        dot3Animation.setOnFinished(e-> {
            dot1Animation.playFromStart();
            dot2Animation.playFromStart();
            dot3Animation.playFromStart();
        });

        dot1Animation.play();
        dot2Animation.play();
        dot3Animation.play();
    }

    /**
     * Create animation for each dot.
     * @param dot is the dot to create animation.
     * @param initialDelay is delay time between two dots animation.
     * @return a SequentialTransition is a cycle of dot animation.
     */
    private SequentialTransition createDotAnimation(Circle dot, int initialDelay) {
        Timeline movement = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dot.translateYProperty(), 0)),
                new KeyFrame(Duration.millis(250), new KeyValue(dot.translateYProperty(), -5)),
                new KeyFrame(Duration.millis(500), new KeyValue(dot.translateYProperty(), 0))
        );

        PauseTransition pause = new PauseTransition(Duration.millis(250));

        SequentialTransition fullCycle = new SequentialTransition(new PauseTransition(Duration.millis(initialDelay)),
                movement, pause);

        fullCycle.setCycleCount(1);

        return fullCycle;
    }

    /**
     * Display customer information in dashboard scene when customer log in.
     */
    private void displayCustomerInformation() {
        if (customer != null) {
            customerFullNameField.setText(customer.getFullName());
            customerUserIDField.setText(customerUserIDField.getText()
                    + String.format("%08d", customer.getUserID()));
        }
    }

    /**
     * Handle animation when swap page on Navigation bar.
     */
    private void handleNavigationBarAction() {
        dashboardButton.getStyleClass().add("selected");
        activeButton = ActiveButton.DASHBOARD;

        dashboardButton.setOnAction(e -> {
            handleNavigationButtonBorder(dashboardButton);
            swapMainContentAnimation(dashboardContent);
            activeButton = ActiveButton.DASHBOARD;
        });
        issueBookButton.setOnAction(e -> {
            handleNavigationButtonBorder(issueBookButton);
            swapMainContentAnimation(issueBookContent);
            activeButton = ActiveButton.ISSUE_BOOK;
        });
        returnBookButton.setOnAction(e -> {
            handleNavigationButtonBorder(returnBookButton);
            swapMainContentAnimation(returnBookContent);
            activeButton = ActiveButton.RETURN_BOOK;
        });
        supportButton.setOnAction(e -> {
            handleNavigationButtonBorder(supportButton);
            swapMainContentAnimation(historyContent);
            activeButton = ActiveButton.HISTORY;
        });
    }

    /**
     * Display top book from database when dashboard initialize.
     * @param topBookDisplayBox container where top book content displayed.
     */
    private void displayTopBook(AnchorPane topBookDisplayBox) {
        Task<Void> loadTopBooksTask = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                Connection connectDB = DatabaseConnection.getConnection();

                if (connectDB == null) {
                    throw new SQLException("Connection is null");
                }

                String query = "SELECT b.title, b.author, t.isbn, b.thumbnail\n" +
                        "FROM transactions t\n" +
                        "JOIN books b ON t.isbn = b.isbn\n" +
                        "GROUP BY t.isbn\n" +
                        "ORDER BY COUNT(b.title and t.isbn) DESC;";

                try (PreparedStatement preparedStatement = connectDB.prepareStatement(query);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
                    int index = 0;
                    while (resultSet.next()) {
                        if (index > 10) break;

                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        String thumbnailUrl = resultSet.getString("thumbnail");
                        String ISBN = "ISBN: " + resultSet.getString("isbn");

                        // Create UI components for each book
                        Pane bookBox = createBookBlock(thumbnailUrl, title, author, ISBN);
                        bookBox.setPrefSize(240, 160);
                        bookBox.setLayoutX(index * 250 + 10);
                        bookBox.setLayoutY(7);

                        // Update the UI on the JavaFX Application Thread
                        javafx.application.Platform.runLater(() -> topBookDisplayBox.getChildren().add(bookBox));

                        index++;
                    }
                    topBookDisplayBox.setPrefWidth(index *  250 + 30);
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

    /**
     * Create a book block contain thumbnail, title,
     * author name, isbn with a button go to view detail of this.
     * @param thumbnailUrl is a String thumbnail image link to display.
     * @param title is title String of book.
     * @param author is name of author.
     * @param ISBN is book's ISBN String.
     * @return a Pane is container of all element.
     */
    private Pane createBookBlock(String thumbnailUrl, String title, String author, String ISBN) {
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setPrefSize(140, 50);
        titleLabel.setLayoutX(100);
        titleLabel.setLayoutY(5);
        titleLabel.setStyle("-fx-text-fill: #000000;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-style: italic;"
                + "-fx-text-alignment: center;"
                + "-fx-alignment: center;");

        Label authorLabel = new Label(author);
        authorLabel.setWrapText(true);
        authorLabel.setPrefSize(85, 35);
        authorLabel.setLayoutX(10);
        authorLabel.setLayoutY(115);
        authorLabel.setStyle("-fx-text-fill: #000000;"
                + "-fx-font-size: 12px;"
                + "-fx-font-style: italic;"
                + "-fx-text-alignment: center;"
                + "-fx-alignment: center;");

        Label ISBNLabel = new Label(ISBN);
        ISBNLabel.setWrapText(true);
        ISBNLabel.setPrefSize(140, 7);
        ISBNLabel.setLayoutX(100);
        ISBNLabel.setLayoutY(75);
        ISBNLabel.setStyle("-fx-alignment: center;");

        Button viewButton = new Button("View");
        viewButton.setPrefSize(115, 40);
        viewButton.setLayoutX(112);
        viewButton.setLayoutY(110);
        viewButton.setStyle("-fx-font-size: 15px;" + "-fx-font-weight: bold;");

        ImageView thumbnailImage = new ImageView();
        if (thumbnailUrl != null) {
            thumbnailImage.setImage(new Image(thumbnailUrl));
            thumbnailImage.setPreserveRatio(false);
            thumbnailImage.setFitHeight(100);
            thumbnailImage.setFitWidth(85);
            thumbnailImage.setLayoutX(10);
            thumbnailImage.setLayoutY(10);
        }

        Pane bookBlock = new Pane(thumbnailImage, titleLabel, authorLabel, ISBNLabel, viewButton);
        bookBlock.getStyleClass().add("dashboard-book-block");

        return bookBlock;
    }

    /**
     * Create a book block contain thumbnail, title,
     * author name, isbn with a button go to view detail of this.
     * @param thumbnailUrl is a String thumbnail image link to display.
     * @param title is title String of book.
     * @param author is name of author.
     * @param ISBN is book's ISBN String.
     * @param description is book's description String.
     * @return a Pane is container of all element.
     */
    private Pane createBookBlock(String thumbnailUrl, String title,
                                 String author, String ISBN, String description) {
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setPrefSize(200, 50);
        titleLabel.setLayoutX(115);
        titleLabel.setLayoutY(10);
        titleLabel.setStyle("-fx-text-fill: #000000;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-style: italic;"
                + "-fx-text-alignment: center;"
                + "-fx-alignment: center;");

        Label authorLabel = new Label(author);
        authorLabel.setWrapText(true);
        authorLabel.setPrefSize(85, 35);
        authorLabel.setLayoutX(10);
        authorLabel.setLayoutY(112);
        authorLabel.setStyle("-fx-text-fill: #000000;"
                + "-fx-font-size: 12px;"
                + "-fx-font-style: italic;"
                + "-fx-text-alignment: center;"
                + "-fx-alignment: center;");

        Label ISBNLabel = new Label(ISBN);
        ISBNLabel.setWrapText(true);
        ISBNLabel.setPrefSize(200, 20);
        ISBNLabel.setLayoutX(115);
        ISBNLabel.setLayoutY(160);
        ISBNLabel.setStyle("-fx-alignment: center;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setPrefSize(200, 100);
        descriptionLabel.setLayoutX(115);
        descriptionLabel.setLayoutY(60);
        ISBNLabel.setStyle("-fx-alignment: center;" + "-fx-text-alignment: center;");

        Button viewButton = new Button("View");
        viewButton.setPrefSize(85, 40);
        viewButton.setLayoutX(10);
        viewButton.setLayoutY(150);
        viewButton.setStyle("-fx-font-size: 15px;" + "-fx-font-weight: bold;");

        ImageView thumbnailImage = new ImageView();
        if (thumbnailUrl != null) {
            thumbnailImage.setImage(new Image(thumbnailUrl));
            thumbnailImage.setPreserveRatio(false);
            thumbnailImage.setFitHeight(100);
            thumbnailImage.setFitWidth(85);
            thumbnailImage.setLayoutX(10);
            thumbnailImage.setLayoutY(10);
        }

        Pane bookBlock = new Pane(thumbnailImage, titleLabel, authorLabel, ISBNLabel, descriptionLabel, viewButton);
        bookBlock.getStyleClass().add("dashboard-book-block");
        return bookBlock;
    }

    /**
     * Display result when customer search. Result will get from Books API.
     * @param resultSearchPane is container of result blocks.
     * @param query is query which customer want to search.
     */
    private void displayResultSearch(AnchorPane resultSearchPane, String query) {
        searchingAnimationPane.setVisible(true);
        loadingAction();

        resultAPISearchScrollPane.setVisible(false);
        resultSearchPane.getChildren().clear();

        Task<List<Volume>> searchTask = new Task<List<Volume>>() {
            @Override
            protected List<Volume> call() throws Exception {
                return GoogleBooksAPIClient.searchBooks(query);
            }
        };

        searchTask.setOnSucceeded(workerStateEvent -> {
            List<Volume> volumes = searchTask.getValue();

            searchingAnimationPane.setVisible(false);
            resultAPISearchScrollPane.setVisible(true);

            if (volumes != null && !volumes.isEmpty()) {
                int blockWidth = 325;
                int blockHeight = 200;
                int padding = 15;
                int spacing = 20;

                for (int index = 0; index < volumes.size(); index++) {
                    Volume volume = volumes.get(index);
                    Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

                    String thumbnailURL = volumeInfo.getImageLinks() != null ?
                            volumeInfo.getImageLinks().getThumbnail() : null;
                    String title = volumeInfo.getTitle();
                    String authors = volumeInfo.getAuthors() != null ?
                            String.join(", ", volumeInfo.getAuthors()) : "Unknown Author";
                    String ISBN = "ISBN: " + GoogleBooksAPIClient.getISBN(volumeInfo);
                    String description = volumeInfo.getDescription();

                    int col = index % 2;
                    int row = index / 2;
                    Task<Pane> blockTask = new Task<Pane>() {
                        @Override
                        protected Pane call() throws Exception {
                            Pane resultBlock = createBookBlock(thumbnailURL, title, authors, ISBN, description);
                            resultBlock.setPrefSize(blockWidth, blockHeight);
                            resultBlock.setLayoutX(padding + col * (blockWidth + spacing));
                            resultBlock.setLayoutY(padding + row * (blockHeight + spacing));
                            return resultBlock;
                        }
                    };

                    blockTask.setOnSucceeded(blockWorker -> {
                        Pane resultBlock = blockTask.getValue();
                        Platform.runLater(() -> resultSearchPane.getChildren().add(resultBlock));
                    });

                    int finalIndex = index;
                    blockTask.setOnFailed(blockWorker -> {
                        System.out.println("Error occurred during create result block where index = " + finalIndex);
                        System.out.println(searchTask.getException().getMessage());
                    });

                    Thread blockThread = new Thread(blockTask);
                    blockThread.setDaemon(true);
                    blockThread.start();
                }

                resultSearchPane.setPrefHeight((volumes.size() / 2.0) * (blockHeight + spacing + padding));
            } else {
                Platform.runLater(() -> {
                    Label notFoundLabel = new Label("No results found");
                    notFoundLabel.setPrefSize(700, 50);
                    notFoundLabel.setLayoutX(100);
                    notFoundLabel.setLayoutY(0);
                    notFoundLabel.setStyle("-fx-font-size: 30px;"
                            + "-fx-text-alignment: center;"
                            + "-fx-alignment: center;");
                    resultSearchPane.getChildren().add(notFoundLabel);
                });
            }
        });

        searchTask.setOnFailed(workerStateEvent -> {
            searchingAnimationPane.setVisible(false);
            resultAPISearchScrollPane.setVisible(false);
            System.out.println("Error occurred during search.");
            System.out.println(searchTask.getException().getMessage());
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }
}
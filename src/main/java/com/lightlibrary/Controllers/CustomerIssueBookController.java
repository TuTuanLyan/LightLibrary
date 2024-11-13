package com.lightlibrary.Controllers;

import com.google.api.services.books.v1.model.Volume;
import com.lightlibrary.Models.GoogleBooksAPIClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
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

    private CustomerDashboardController parentController;

    public CustomerDashboardController getParentController() {
        return parentController;
    }

    public void setParentController(CustomerDashboardController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        detailBookPane.setVisible(false);
        detailCloseButton.setOnAction(e -> detailBookPane.setVisible(false));
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
                String ISBN = "ISBN: " + GoogleBooksAPIClient.getISBN(volumeInfo);
                String description = volumeInfo.getDescription() != null ?
                        volumeInfo.getDescription() : "There is no description for this book :(";
                String publisher = volumeInfo.getPublisher() != null ?
                        "Publish by " + volumeInfo.getPublisher() : "Unknown Publisher";
                String publishedDate = volumeInfo.getPublishedDate() != null ?
                        "Publish at " + volumeInfo.getPublishedDate() : "Unknown published date";


                int blockIndex = index;
                Task<Pane> blockTask = new Task<Pane>() {
                    @Override
                    protected Pane call() throws Exception {
                        Pane bookPane = ControllerUntil.createBookBlock(thumbnailURL, title, authors, ISBN, description);
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
        System.out.println("Call here " + ISBN);

        detailBookPane.setVisible(true);

        detailThumbnailImage.setImage(new Image(thumbnailUrl));
        detailAuthorLabel.setText(author);
        detailTItleLabel.setText(title);
        detailDescriptionLabel.setText(description);
        detailISBNLabel.setText(ISBN);
        detailPublisherLabel.setText(publisher);
        detailPublishDateLabel.setText(publishDate);
    }

    private Button createViewDetailButton() {
        Button viewDetailButton = new Button("View Detail");
        viewDetailButton.setPrefSize(120, 40);
        viewDetailButton.setLayoutX(450);
        viewDetailButton.setLayoutY(180);
        viewDetailButton.getStyleClass().add("view-detail-button");

        return viewDetailButton;
    }
}

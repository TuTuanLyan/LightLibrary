package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CustomerDashboardController implements Initializable {

    @FXML
    private AnchorPane dashBoardRoot;

    @FXML
    private AnchorPane mainContentContainer;
    private Map<String, FXMLLoader> cache = new HashMap<>();

    @FXML
    private Label currentPageNameLabel;

    @FXML
    private Pane avatarImageContainer;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerCoinAmoutLabel;

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button issueBookButton;

    @FXML
    private Button returnBookButton;

    @FXML
    private Button historyButton;

    @FXML
    private ImageView homeImage;

    @FXML
    private ImageView issueBookImage;

    @FXML
    private ImageView returnBookImage;

    @FXML
    private ImageView historyImage;

    @FXML
    private Pane navigationBorderPane;

    private Node currentNode;

    public enum ActiveButton {
        HOME,
        ISSUE_BOOK,
        RETURN_BOOK,
        HISTORY
    }

    public ActiveButton activeButton;

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        displayCustomerInformation();
        setTheme(customer.isDarkMode());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preLoad();

        loadPane("/com/lightlibrary/Views/CustomerHome.fxml");
        activeButton = ActiveButton.HOME;
        currentPageNameLabel.setText("Dashboard");

        searchBar.setOnAction(event -> {
            String query = searchBar.getText();
            updateIssueBookSearchResults(query);
        });
        searchButton.setOnAction(event -> {
            String query = searchBar.getText();
            updateIssueBookSearchResults(query);
        });

        navigationButtonAction();
    }

    private void preLoad() {
        String[] fxmlPaths = {
                "/com/lightlibrary/Views/CustomerHome.fxml",
                "/com/lightlibrary/Views/CustomerIssueBook.fxml",
                "/com/lightlibrary/Views/CustomerReturnBook.fxml",
                "/com/lightlibrary/Views/CustomerHistory.fxml"
        };

        for (String fxmlPath : fxmlPaths) {
            Task<FXMLLoader> loadTask = new Task<>() {
                @Override
                protected FXMLLoader call() throws IOException {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    loader.load();
                    return loader;
                }
            };

            loadTask.setOnSucceeded(event -> {
                FXMLLoader loader = loadTask.getValue();
                cache.put(fxmlPath, loader);
            });

            loadTask.setOnFailed(event -> loadTask.getException().printStackTrace());

            Thread loadPaneThread = new Thread(loadTask);
            loadPaneThread.setDaemon(true);
            loadPaneThread.start();
        }
    }

    /**
     * Load pane in another thread when customer switch page.
     * @param fxmlPath is the path to scene which customer want to go.
     */
    private void loadPane(final String fxmlPath) {
        if (cache.containsKey(fxmlPath)) {
            FXMLLoader loader = (FXMLLoader) cache.get(fxmlPath);
            setPaneWithAnimation(loader.getRoot());
        } else {
            Task<FXMLLoader> loadTask = new Task<>() {
                @Override
                protected FXMLLoader call() throws IOException {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    loader.load();
                    return loader;
                }
            };

            loadTask.setOnSucceeded(event -> {
                FXMLLoader loader = loadTask.getValue();
                cache.put(fxmlPath, loader);
                setPaneWithAnimation(loader.getRoot());
            });

            loadTask.setOnFailed(event -> loadTask.getException().printStackTrace());

            Thread loadPaneThread = new Thread(loadTask);
            loadPaneThread.setDaemon(true);
            loadPaneThread.start();
        }
    }

    /**
     * Create animation when switch dashboard page.
     * @param newNode is Node of the page which customer want to go.
     */
    private void setPaneWithAnimation(Node newNode) {
        if (currentNode != null) {
            FadeTransition fadeOut = ControllerUntil.creatFadeOutAnimation(currentNode);
            fadeOut.setOnFinished(e -> {
                mainContentContainer.getChildren().clear();
                mainContentContainer.getChildren().add(newNode);
                FadeTransition fadeIn = ControllerUntil.creatFadeInAnimation(newNode);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            mainContentContainer.getChildren().add(newNode);
            FadeTransition fadeIn = ControllerUntil.creatFadeInAnimation(newNode);
            fadeIn.play();
        }
        currentNode = newNode;
    }

    public void goToHomePage() {
        if (activeButton != ActiveButton.HOME) {
            loadPane("/com/lightlibrary/Views/CustomerHome.fxml");
            currentPageNameLabel.setText("Home");
            navigationBorderAnimation(homeButton);
            activeButton = ActiveButton.HOME;
        }
    }

    public void goToIssueBookPage() {
        if (activeButton != ActiveButton.ISSUE_BOOK) {
            loadPane("/com/lightlibrary/Views/CustomerIssueBook.fxml");
            currentPageNameLabel.setText("Issue Book");
            navigationBorderAnimation(issueBookButton);
            activeButton = ActiveButton.ISSUE_BOOK;
        }
    }

    public void goToReturnBookPage() {
        if (activeButton != ActiveButton.RETURN_BOOK) {
            loadPane("/com/lightlibrary/Views/CustomerReturnBook.fxml");
            currentPageNameLabel.setText("Return Book");
            navigationBorderAnimation(returnBookButton);
            activeButton = ActiveButton.RETURN_BOOK;
        }
    }

    public void goToHistoryPage() {
        if (activeButton != ActiveButton.HISTORY) {
            loadPane("/com/lightlibrary/Views/CustomerHistory.fxml");
            currentPageNameLabel.setText("History");
            navigationBorderAnimation(historyButton);
            activeButton = ActiveButton.HISTORY;
        }
    }

    public void changeTheme() {
        this.customer.setDarkMode(!this.customer.isDarkMode());
        dashBoardRoot.getStylesheets().clear();
        setTheme(this.customer.isDarkMode());
    }

    private void setTheme(boolean darkMode) {
        if (darkMode) {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-home.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-book-issue.png")).toExternalForm()));
            returnBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-return.png")).toExternalForm()));
            historyImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-history.png")).toExternalForm()));
        } else {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-home.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-book-issue.png")).toExternalForm()));
            returnBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-return.png")).toExternalForm()));
            historyImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-history.png")).toExternalForm()));
        }
    }

    private void displayCustomerInformation() {
        Circle avatarClip = new Circle(35, 35, 35);
        avatarImageContainer.setClip(avatarClip);

        if (customer != null) {
            customerNameLabel.setText(this.customer.getFullName() == null ? "Name of customer" : this.customer.getFullName());
            customerCoinAmoutLabel.setText(this.customer.getCoins() + "");
        }
    }

    private void navigationButtonAction() {
        homeButton.setOnAction(event -> {
            goToHomePage();
        });

        issueBookButton.setOnAction(event -> {
            goToIssueBookPage();
        });

        returnBookButton.setOnAction(event -> {
            goToReturnBookPage();
        });

        historyButton.setOnAction(event -> {
            goToHistoryPage();
        });
    }

    private void navigationBorderAnimation(Button activeButton) {
        TranslateTransition navigationButtonBorderTransition = new TranslateTransition(Duration.seconds(0.2),
                navigationBorderPane);
        navigationButtonBorderTransition.setToY(activeButton.getLayoutY() - 10 - navigationBorderPane.getLayoutY());
        navigationButtonBorderTransition.play();
    }

    public void updateIssueBookSearchResults(String query) {
        String fxmlPath = "/com/lightlibrary/Views/CustomerIssueBook.fxml";

        if (cache.containsKey(fxmlPath)) {
            FXMLLoader loader = cache.get(fxmlPath);
            CustomerIssueBookController controller = loader.getController();
            controller.updateSearchResults(query);
        } else {
            loadPane(fxmlPath);
            Platform.runLater(() -> {
                FXMLLoader loader = cache.get(fxmlPath);
                if (loader != null) {
                    CustomerIssueBookController controller = loader.getController();
                    controller.updateSearchResults(query);
                }
            });
        }

        goToIssueBookPage();
    }


    public void goToSetting(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/lightlibrary/Views/Setting.fxml"));
            Parent setting = (Parent) loader.load();
            SettingController controller = loader.getController();
            controller.setCustomerDashboardController(CustomerDashboardController.this);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Platform.runLater(stage::centerOnScreen);
            stage.setScene(new Scene(setting, 960, 640));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

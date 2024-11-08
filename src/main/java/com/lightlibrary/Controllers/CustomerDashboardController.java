package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerDashboardController implements Initializable {

    @FXML
    private AnchorPane dashBoardRoot;

    @FXML
    private AnchorPane mainContentContainer;

    @FXML
    private ImageView homeImage;

    @FXML
    private ImageView issueBookImage;

    @FXML
    private ImageView returnBookImage;

    @FXML
    private ImageView historyImage;

    private Node currentNode;
    private final Map<String, Node> paneCache = new HashMap<>();

    public enum ActiveButton {
        HOME,
        ISSUE_BOOK,
        RETURN_BOOK,
        HISTORY
    }

    public ActiveButton activeButton;

    private Customer customer;

    private boolean darkMode = false;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPane("/com/lightlibrary/Views/CustomerHome.fxml");
        activeButton = ActiveButton.HOME;
        setTheme(darkMode);
    }

    /**
     * Load pane in another thread when customer switch page.
     * @param fxmlPath is the path to scene which customer want to go.
     */
    private void loadPane(final String fxmlPath) {
        if (paneCache.containsKey(fxmlPath)) {
            setPaneWithAnimation(paneCache.get(fxmlPath));
        } else {
            Task<Node> loadTask = new Task<>() {
                @Override
                protected Node call() throws IOException {
                    return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                }
            };

            loadTask.setOnSucceeded(event -> {
                Node pane = loadTask.getValue();
                pane.setLayoutX(0);
                pane.setLayoutY(0);
                paneCache.put(fxmlPath, pane);
                setPaneWithAnimation(pane);
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
            activeButton = ActiveButton.HOME;
        }
    }

    public void goToIssueBookPage() {
        if (activeButton != ActiveButton.ISSUE_BOOK) {
            loadPane("/com/lightlibrary/Views/CustomerIssueBook.fxml");
            activeButton = ActiveButton.ISSUE_BOOK;
        }
    }

    public void goToReturnBookPage() {
        if (activeButton != ActiveButton.RETURN_BOOK) {
            loadPane("/com/lightlibrary/Views/CustomerReturnBook.fxml");
            activeButton = ActiveButton.RETURN_BOOK;
        }
    }

    public void goToHistoryPage() {
        if (activeButton != ActiveButton.HISTORY) {
            loadPane("/com/lightlibrary/Views/CustomerHistory.fxml");
            activeButton = ActiveButton.HISTORY;
        }
    }

    public void changeTheme() {
        this.darkMode = !this.darkMode;
        dashBoardRoot.getStylesheets().clear();
        setTheme(this.darkMode);
    }

    private void setTheme(boolean darkMode) {
        if (darkMode) {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/light-home.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/light-book-issue.png")).toExternalForm()));
            returnBookImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/light-return.png")).toExternalForm()));
            historyImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/light-history.png")).toExternalForm()));
        } else {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/dark-home.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/dark-book-issue.png")).toExternalForm()));
            returnBookImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/dark-return.png")).toExternalForm()));
            historyImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/dark-history.png")).toExternalForm()));
        }
    }
}

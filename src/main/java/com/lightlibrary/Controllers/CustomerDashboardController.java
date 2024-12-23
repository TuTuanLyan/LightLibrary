package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CustomerDashboardController implements Initializable {

    @FXML
    private AnchorPane dashBoardRoot;

    @FXML
    private AnchorPane mainContentContainer;
    private Map<String, FXMLLoader> cache = new HashMap<>();

    private Node currentNode;

    @FXML
    private Button changeThemeButton;
    @FXML
    private Circle changeThemeToggle;

    @FXML
    private Label currentPageNameLabel;

    @FXML
    private Pane avatarImageContainer;

    @FXML
    private ImageView avatarImage;

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
    private Button chatButton;

    @FXML
    private ImageView homeImage;

    @FXML
    private ImageView issueBookImage;

    @FXML
    private ImageView returnBookImage;

    @FXML
    private ImageView historyImage;

    @FXML
    private ImageView chatImage;

    @FXML
    private Pane navigationBorderPane;

    public enum ActiveButton {
        HOME,
        ISSUE_BOOK,
        RETURN_BOOK,
        HISTORY,
        CHAT
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

        preLoad();
        goToHomePage();

        changeThemeToggleButtonAnimation(customer.isDarkMode());
    }

    CustomerChatController customerChatController;

    public void setCustomerChatController(CustomerChatController customerChatController) {
        this.customerChatController = customerChatController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchBar.setOnAction(event -> {
            String query = searchBar.getText();
            updateIssueBookSearchResults(query);
        });
        searchButton.setOnAction(event -> {
            String query = searchBar.getText();
            updateIssueBookSearchResults(query);
        });

        changeThemeButton.setOnAction(event -> {
            changeTheme();
        });

        navigationButtonAction();
    }

    private void preLoad() {
        String[] fxmlPaths = {
                "/com/lightlibrary/Views/CustomerHome.fxml",
                "/com/lightlibrary/Views/CustomerIssueBook.fxml",
                "/com/lightlibrary/Views/CustomerReturnBook.fxml",
                "/com/lightlibrary/Views/CustomerHistory.fxml",
                "/com/lightlibrary/Views/CustomerChat.fxml"
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
                Object controller = loader.getController();
                if (controller instanceof SyncAction) {
                    ((SyncAction) controller).setTheme(customer.isDarkMode());
                    changeThemeToggleButtonAnimation(customer.isDarkMode());
                    ((SyncAction) controller).setParentController(this);
                    ((SyncAction) controller).autoUpdate();
                }
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
            Object controller = loader.getController();
            if (controller instanceof SyncAction) {
                ((SyncAction) controller).setTheme(customer.isDarkMode());
                ((SyncAction) controller).setParentController(this);
                ((SyncAction) controller).autoUpdate();
            }
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

                Object controller = loader.getController();
                if (controller instanceof SyncAction) {
                    ((SyncAction) controller).setTheme(customer.isDarkMode());
                    changeThemeToggleButtonAnimation(customer.isDarkMode());
                    ((SyncAction) controller).setParentController(this);
                    ((SyncAction) controller).autoUpdate();
                }
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
            FadeTransition fadeOut = ControllerUtil.creatFadeOutAnimation(currentNode);
            fadeOut.setOnFinished(e -> {
                mainContentContainer.getChildren().clear();
                mainContentContainer.getChildren().add(newNode);
                FadeTransition fadeIn = ControllerUtil.creatFadeInAnimation(newNode);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            mainContentContainer.getChildren().add(newNode);
            FadeTransition fadeIn = ControllerUtil.creatFadeInAnimation(newNode);
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

    public void goToChatPage() {
        if (activeButton != ActiveButton.CHAT) {
            loadPane("/com/lightlibrary/Views/CustomerChat.fxml");
            currentPageNameLabel.setText("Chat");
            navigationBorderAnimation(chatButton);
            activeButton = ActiveButton.CHAT;
        }
    }

    public void changeTheme() {
        this.customer.setDarkMode(!this.customer.isDarkMode());
        dashBoardRoot.getStylesheets().clear();
        setTheme(this.customer.isDarkMode());
        changeThemeToggleButtonAnimation(this.customer.isDarkMode());

        debounceSaveTheme(this.customer.isDarkMode());
    }

    private void debounceSaveTheme(boolean isDarkMode) {
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            saveThemeToDatabase(isDarkMode);
            scheduler.shutdown(); // Shutdown sau khi task hoàn thành
        }, 800, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!scheduler.isShutdown()) {
                future.cancel(false);
                scheduler.shutdownNow();
            }
        }));
    }

    private void saveThemeToDatabase(boolean isDarkMode) {
        try {
            String sql = "UPDATE users SET darkMode = ? WHERE userID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setBoolean(1, isDarkMode);
                stmt.setInt(2, this.customer.getUserID());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeThemeToggleButtonAnimation(boolean darkMode) {
        TranslateTransition changeThemeButtonTransition = new TranslateTransition(Duration.seconds(0.3), changeThemeToggle);
        if (darkMode) {
            changeThemeButton.setText("Dark Mode");
            changeThemeButtonTransition.setToX(-33);
            changeThemeButtonTransition.play();

            Color startColor = Color.web("#ffffff");
            Color endColor = Color.web("#434343");
            FillTransition changeThemeButtonFillTransition = new FillTransition(Duration.seconds(0.3),
                    changeThemeToggle, startColor, endColor);
            changeThemeButtonFillTransition.play();
        } else {
            changeThemeButton.setText("Light Mode");
            changeThemeButtonTransition.setToX(0);
            changeThemeButtonTransition.play();

            Color startColor = Color.web("#434343");
            Color endColor = Color.web("#ffffff");
            FillTransition changeThemeButtonFillTransition = new FillTransition(Duration.seconds(0.3),
                    changeThemeToggle, startColor, endColor);
            changeThemeButtonFillTransition.play();
        }
    }

    private void setTheme(boolean darkMode) {
        updateChildThemes(darkMode);

        dashBoardRoot.getStylesheets().clear();
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
            chatImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-chat.png")).toExternalForm()));
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
            chatImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-chat.png")).toExternalForm()));
        }
    }

    private void updateChildThemes(boolean darkMode) {
        cache.values().forEach(loader -> {
            Object controller = loader.getController();
            if (controller instanceof SyncAction) {
                ((SyncAction) controller).setTheme(darkMode);
            }
        });
    }

    private void displayCustomerInformation() {
        Circle avatarClip = new Circle(35, 35, 35);
        avatarImageContainer.setClip(avatarClip);

        String avatarUrl = customer.getAvatarImageUrl();
        if (avatarUrl != null) {
            if (!avatarUrl.startsWith("file:")) {
                avatarUrl = new File(avatarUrl).toURI().toString();
            }
        } else {
            avatarUrl = Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/LightLibraryLogo.png")).toExternalForm();
        }

        Image avatar = new Image(avatarUrl);
        avatarImage.setImage(avatar);

        if (customer != null) {
            customerNameLabel.setText(this.customer.getFullName() == null ?
                    "Name of customer" : this.customer.getFullName());
            customerCoinAmoutLabel.setText(this.customer.getCoins() + "");
        }
    }

    public void updateCoin(double coins) {
        Platform.runLater(() -> customerCoinAmoutLabel.setText(coins + ""));
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

        chatButton.setOnAction(event -> {
            goToChatPage();
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
                    .getResource("/com/lightlibrary/Views/CustomerSetting.fxml"));
            Parent setting = (Parent) loader.load();
            CustomerSettingController controller = loader.getController();
            controller.setCustomerDashboardController(CustomerDashboardController.this);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Platform.runLater(stage::centerOnScreen);
            stage.setScene(new Scene(setting, 960, 640));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void goToGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/lightlibrary/Views/GameView.fxml"));
            Parent setting = (Parent) loader.load();
            GameController controller = loader.getController();
            controller.setCustomer(this.customer);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Platform.runLater(stage::centerOnScreen);
            stage.setScene(new Scene(setting, 960, 640));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void logout(ActionEvent event) throws IOException {
        if (customerChatController != null) {
            customerChatController.autoDisconnect();
        }
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setOnCloseRequest(_ -> {
            if (customerChatController != null) {
                customerChatController.autoDisconnect();
            }
            Platform.exit();
            System.exit(0);
        });
        Platform.runLater(stage::centerOnScreen);
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }
}

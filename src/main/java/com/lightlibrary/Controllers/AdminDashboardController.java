package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Admin;
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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private AnchorPane dashBoardRoot;

    @FXML
    private AnchorPane mainContentContainer;
    private Map<String, FXMLLoader> cache = new HashMap<>();

    private Node currentNode;

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton;

    @FXML
    private Button changeThemeButton;
    @FXML
    private Circle changeThemeToggle;

    @FXML
    private Label currentPageNameLabel;

    @FXML
    private Pane avatarImageContainer;

    @FXML
    private Label adminName;

    @FXML
    private Button homeButton;

    @FXML
    private Button viewBookButton;

    @FXML
    private Button issueBookButton;

    @FXML
    private Button userButton;

    @FXML
    private Button chatButton;

    @FXML
    private ImageView homeImage;

    @FXML
    private ImageView viewBookImage;

    @FXML
    private ImageView issueBookImage;

    @FXML
    private ImageView userImage;

    @FXML
    private ImageView chatImage;

    @FXML
    private Pane navigationBorderPane;

    public enum ActiveButton {
        HOME,
        VIEW_BOOK,
        ISSUE_BOOK,
        USER_MANAGEMENT,
        CHAT
    }

    public ActiveButton activeButton;

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        displayAdminInformation();
        setTheme(admin.isDarkMode());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preLoad();

        loadPane("/com/lightlibrary/Views/AdminHome.fxml");
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

        changeThemeButton.setOnAction(event -> {
            changeTheme();
        });

        navigationButtonAction();
    }

    private void displayAdminInformation() {
        Circle avatarClip = new Circle(35, 35, 35);
        avatarImageContainer.setClip(avatarClip);

        if (admin != null) {
            adminName.setText(this.admin.getFullName() == null ?
                    "Name of customer" : this.admin.getFullName());
        }
    }

    private void preLoad() {
        String[] fxmlPaths = {
                "/com/lightlibrary/Views/AdminHome.fxml",
                "/com/lightlibrary/Views/AdminViewBook.fxml",
                "/com/lightlibrary/Views/AdminIssueBook.fxml",
                "/com/lightlibrary/Views/AdminUserManagement.fxml",
                "/com/lightlibrary/Views/AdminChat.fxml"
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

    private void loadPane(final String fxmlPath) {
        if (cache.containsKey(fxmlPath)) {
            FXMLLoader loader = (FXMLLoader) cache.get(fxmlPath);
            setPaneWithAnimation(loader.getRoot());
            Object controller = loader.getController();
            if (controller instanceof SyncAction) {
                ((SyncAction) controller).setTheme(admin.isDarkMode());
                ((SyncAction) controller).setParentController(this);
            }
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
                    ((SyncAction) controller).setTheme(admin.isDarkMode());
                    ((SyncAction) controller).setParentController(this);
                }
            });

            loadTask.setOnFailed(event -> loadTask.getException().printStackTrace());

            Thread loadPaneThread = new Thread(loadTask);
            loadPaneThread.setDaemon(true);
            loadPaneThread.start();
        }
    }

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
            loadPane("/com/lightlibrary/Views/AdminHome.fxml");
            currentPageNameLabel.setText("Home");
            navigationBorderAnimation(homeButton);
            activeButton = ActiveButton.HOME;
        }
    }

    public void goToViewBookPage() {
        if (activeButton != ActiveButton.VIEW_BOOK) {
            loadPane("/com/lightlibrary/Views/AdminViewBook.fxml");
            currentPageNameLabel.setText("View Book");
            navigationBorderAnimation(viewBookButton);
            activeButton = ActiveButton.VIEW_BOOK;
        }
    }

    public void goToIssueBookPage() {
        if (activeButton != ActiveButton.ISSUE_BOOK) {
            loadPane("/com/lightlibrary/Views/AdminIssueBook.fxml");
            currentPageNameLabel.setText("Issue Book");
            navigationBorderAnimation(issueBookButton);
            activeButton = ActiveButton.ISSUE_BOOK;
        }
    }

    public void goToUserManagementPage() {
        if (activeButton != ActiveButton.USER_MANAGEMENT) {
            loadPane("/com/lightlibrary/Views/AdminUserManagement.fxml");
            currentPageNameLabel.setText("User Management");
            navigationBorderAnimation(userButton);
            activeButton = ActiveButton.USER_MANAGEMENT;
        }
    }

    public void goToChatPage() {
        if (activeButton != ActiveButton.CHAT) {
            loadPane("/com/lightlibrary/Views/AdminChat.fxml");
            currentPageNameLabel.setText("Chat");
            navigationBorderAnimation(chatButton);
            activeButton = ActiveButton.CHAT;
        }
    }

    private void navigationButtonAction() {
        homeButton.setOnAction(event -> {
            goToHomePage();
        });

        viewBookButton.setOnAction(event -> {
            goToViewBookPage();
        });

        issueBookButton.setOnAction(event -> {
            goToIssueBookPage();
        });

        userButton.setOnAction(event -> {
            goToUserManagementPage();
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
        String fxmlPath = "/com/lightlibrary/Views/AdminIssueBook.fxml";

        if (cache.containsKey(fxmlPath)) {
            FXMLLoader loader = cache.get(fxmlPath);
            AdminIssueBookController controller = loader.getController();
            controller.updateSearchResults(query);
        } else {
            loadPane(fxmlPath);
            Platform.runLater(() -> {
                FXMLLoader loader = cache.get(fxmlPath);
                if (loader != null) {
                    AdminIssueBookController controller = loader.getController();
                    controller.updateSearchResults(query);
                }
            });
        }

        goToIssueBookPage();
    }

    public void changeTheme() {
        this.admin.setDarkMode(!this.admin.isDarkMode());
        dashBoardRoot.getStylesheets().clear();
        setTheme(this.admin.isDarkMode());
        changeThemeToggleButtonAnimation(this.admin.isDarkMode());
    }

    private void setTheme(boolean darkMode) {
        dashBoardRoot.getStylesheets().clear();
        if (darkMode) {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/dark-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-home.png")).toExternalForm()));
            viewBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-book-issue.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-borrowed-book.png")).toExternalForm()));
            userImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-user.png")).toExternalForm()));
            chatImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/light-help.png")).toExternalForm()));
        } else {
            dashBoardRoot.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/StyleSheets/light-theme.css")).toExternalForm());
            homeImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-home.png")).toExternalForm()));
            viewBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-book-issue.png")).toExternalForm()));
            issueBookImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-borrowed-book.png")).toExternalForm()));
            userImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-user.png")).toExternalForm()));
            chatImage.setImage(new Image(Objects.requireNonNull(getClass()
                    .getResource("/com/lightlibrary/Images/dark-help.png")).toExternalForm()));
        }

        updateChildThemes(darkMode);
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

    private void updateChildThemes(boolean darkMode) {
        cache.values().forEach(loader -> {
            Object controller = loader.getController();
            if (controller instanceof SyncAction) {
                ((SyncAction) controller).setTheme(darkMode);
            }
        });
    }

    public void logout(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Platform.runLater(stage::centerOnScreen);
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }
}

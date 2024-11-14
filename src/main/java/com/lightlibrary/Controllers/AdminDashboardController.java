package com.lightlibrary.Controllers;

import javafx.animation.FadeTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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
    private Button changeThemeButton;
    @FXML
    private Circle changeThemeToggle;

    @FXML
    private Label currentPageNameLabel;

    @FXML
    private Pane avatarImageContainer;

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

    public enum ActiveButton {
        HOME,
        VIEW_BOOK,
        ISSUE_BOOK,
        USER_MANAGEMENT,
        CHAT
    }

    public ActiveButton activeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigationButtonAction();
    }

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

                /*Object controller = loader.getController();
                if (controller instanceof SyncAction) {
                    ((SyncAction) controller).setTheme(customer.isDarkMode());
                    ((SyncAction) controller).setParentController(this);
                }*/
            });

            loadTask.setOnFailed(event -> loadTask.getException().printStackTrace());

            Thread loadPaneThread = new Thread(loadTask);
            loadPaneThread.setDaemon(true);
            loadPaneThread.start();
        }
    }

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
            loadPane("/com/lightlibrary/Views/AdminHome.fxml");
            currentPageNameLabel.setText("Home");
            //navigationBorderAnimation(homeButton);
            activeButton = ActiveButton.HOME;
        }
    }

    public void goToViewBookPage() {
        if (activeButton != ActiveButton.VIEW_BOOK) {
            loadPane("/com/lightlibrary/Views/AdminViewBook.fxml");
            currentPageNameLabel.setText("Return Book");
            //navigationBorderAnimation(returnBookButton);
            activeButton = ActiveButton.VIEW_BOOK;
        }
    }
    public void goToIssueBookPage() {
        if (activeButton != ActiveButton.ISSUE_BOOK) {
            loadPane("/com/lightlibrary/Views/AdminIssueBook.fxml");
            currentPageNameLabel.setText("Issue Book");
            //navigationBorderAnimation(issueBookButton);
            activeButton = ActiveButton.ISSUE_BOOK;
        }
    }

    public void goToUserManagementPage() {
        if (activeButton != ActiveButton.USER_MANAGEMENT) {
            loadPane("/com/lightlibrary/Views/AdminUSerManagement.fxml");
            currentPageNameLabel.setText("History");
            //navigationBorderAnimation(historyButton);
            activeButton = ActiveButton.USER_MANAGEMENT;
        }
    }

    public void goToChatPage() {
        if (activeButton != ActiveButton.CHAT) {
            loadPane("/com/lightlibrary/Views/AdminChat.fxml");
            currentPageNameLabel.setText("Chat");
            //navigationBorderAnimation(chatButton);
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

    public void logout(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("/com/lightlibrary/Views/LoginAndRegister.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Platform.runLater(stage::centerOnScreen);
        stage.setScene(new Scene(login, 960, 640));
        stage.show();
    }
}

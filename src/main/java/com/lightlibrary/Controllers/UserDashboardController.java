package com.lightlibrary.Controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML
    private Pane avatarContainer;

    @FXML
    private ImageView avatarImage;

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
    private AnchorPane mainContentContainer;

    @FXML
    private Pane dashboardContent;

    @FXML
    private Pane issueBookContent;

    @FXML
    private Pane returnBookContent;

    @FXML
    private Pane supportContent;

    /**
     * Enum representing the active navigation button on the dashboard.
     */
    enum ActiveButton {
        DASHBOARD,
        ISSUE_BOOK,
        RETURN_BOOK,
        SUPPORT
    }

    private static ActiveButton activeButton;

    /**
     * Initializes the User Dashboard controller, setting up event handlers and UI elements.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        individualButtonContainer.setVisible(false);
        Circle avatarClip = new Circle(28, 28, 28);
        avatarContainer.setClip(avatarClip);

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
            swapMainContentAnimation(supportContent);
            activeButton = ActiveButton.SUPPORT;
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
     * Handle Animation in main content when swapped.
     * @param newContent is the new content Pane user want to go.
     */
    private void swapMainContentAnimation(Pane newContent) {
        Pane currentContent = switch (activeButton) {
            case DASHBOARD -> dashboardContent;
            case ISSUE_BOOK -> issueBookContent;
            case RETURN_BOOK -> returnBookContent;
            case SUPPORT -> supportContent;
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
}
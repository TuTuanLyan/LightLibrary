package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
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
    private Pane dashboardContent;

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
}
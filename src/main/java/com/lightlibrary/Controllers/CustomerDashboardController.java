package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomerDashboardController implements Initializable {

    @FXML
    private AnchorPane mainContentContainer;

    private Node currentNode;
    private final Map<String, Node> paneCache = new HashMap<>();

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPane("/com/lightlibrary/Views/CustomerHome.fxml");
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
        loadPane("/com/lightlibrary/Views/CustomerHome.fxml");
    }

    public void goToIssueBookPage() {
        loadPane("/com/lightlibrary/Views/CustomerIssueBook.fxml");
    }

    public void goToReturnBookPage() {
        loadPane("/com/lightlibrary/Views/CustomerReturnBook.fxml");
    }

    public void goToHistoryPage() {
        loadPane("/com/lightlibrary/Views/CustomerHistory.fxml");
    }
}

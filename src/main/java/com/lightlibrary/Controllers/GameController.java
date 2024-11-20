package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Customer;
import com.lightlibrary.Models.DatabaseConnection;
import com.lightlibrary.Models.Game.Game;
import com.lightlibrary.Models.Sound;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GameController {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Label scoreLabel, collectedCoinLabel, pausedMenuScoreLabel, pausedMenuCollectedCoinLabel, gameOverMenuScoreLabel, gameOverMenuCollectedCoinLabel;
    @FXML
    private ImageView heart1, heart2, heart3;
    @FXML
    private ImageView armor1, armor2, armor3;
    @FXML
    private VBox pauseMenu, gameOverMenu, storyBox;

    Customer customer;

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private Game game;

    Customer getCustomer(Customer customer) {
        return this.customer;
    }

    void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void initialize() {
        game = new Game();
        game.setController(this);

        Sound.initialize();

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        game.startGame(gc);
        game.render(gc);

        setupKeyAndMouseListeners();

        // Pause game tạm thời để hiển thị story
        isPaused = true;
        game.setPaused(isPaused);
        gameCanvas.setMouseTransparent(isPaused);
    }

    private void setupKeyAndMouseListeners() {
        Scene scene = gameCanvas.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
            scene.setOnMouseClicked(this::handleMouseClick);
        } else {
            gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(this::handleKeyPress);
                    newScene.setOnMouseClicked(this::handleMouseClick);
                }
            });
        }
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE -> {
                Sound.playSoundEffect("jump");
                game.jump();
            }
            case ESCAPE -> {
                Sound.playSoundEffect("button");
                togglePause();
            }
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !isPaused && !isGameOver) {
            Sound.playSoundEffect("jump");
            game.jump();
        }
    }

    @FXML
    private void togglePause() {
        if (!isGameOver) {
            isPaused = !isPaused;
            pauseMenu.setVisible(isPaused);
            game.setPaused(isPaused);
            gameCanvas.setMouseTransparent(isPaused);
            scoreLabel.setVisible(!isPaused);
            collectedCoinLabel.setVisible(!isPaused);
            if (isPaused) {
                Sound.pauseBackgroundMusic();
            } else {
                Sound.playBackgroundMusic();
            }
        }
    }

    @FXML
    private void restartGame() {
        Sound.stopBackgroundMusic();
        Sound.playBackgroundMusic();
        isGameOver = false;
        isPaused = false;
        gameOverMenu.setVisible(false);
        pauseMenu.setVisible(false);
        scoreLabel.setVisible(true);
        collectedCoinLabel.setVisible(true);
        game.restart();
    }

    @FXML
    private void handleStartGameButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Sound.playSoundEffect("button");
            Sound.playBackgroundMusic();
            storyBox.setVisible(false);
            isPaused = false;
            game.setPaused(isPaused);
            gameCanvas.setMouseTransparent(isPaused);
        }
    }

    @FXML
    private void handleResumeButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isPaused && !isGameOver) {
            Sound.playSoundEffect("button");
            togglePause();
        }
    }

    @FXML
    private void handleRestartButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Sound.playSoundEffect("button");
            togglePause();
            restartGame();
        }
    }

    @FXML
    private void handleRetryButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isGameOver) {
            Sound.playSoundEffect("button");
            restartGame();
        }
    }

    @FXML
    private void handleReturnButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && (isGameOver || isPaused)) {
            Sound.playSoundEffect("button");
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/com/lightlibrary/Views/CustomerDashboard.fxml"));
                Parent dashboard = loader.load();
                CustomerDashboardController controller = loader.getController();
                controller.setCustomer(this.customer);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Platform.runLater(stage::centerOnScreen);
                stage.setScene(new Scene(dashboard, 1440, 900));
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
        pausedMenuScoreLabel.setText("Current score: " + score);
        gameOverMenuScoreLabel.setText("Final score: " + score);
    }

    public void updateCollectedCoin(int collectedCoin) {
        collectedCoinLabel.setText("Coin collected: " + collectedCoin);
        pausedMenuCollectedCoinLabel.setText("Coin collected: " + collectedCoin);
        gameOverMenuCollectedCoinLabel.setText("Coin earned: " + collectedCoin);
    }

    public void updateHealth(int health) {
        heart1.setVisible(health > 0);
        heart2.setVisible(health > 1);
        heart3.setVisible(health > 2);
    }

    public void updateArmor(int armor) {
        armor1.setVisible(armor > 0);
        armor2.setVisible(armor > 1);
        armor3.setVisible(armor > 2);
    }

    public void showGameOver() {
        isGameOver = true;
        scoreLabel.setVisible(false);
        collectedCoinLabel.setVisible(false);
        gameOverMenu.setVisible(true);
        gameCanvas.setMouseTransparent(true);
        Sound.stopBackgroundMusic();
        Sound.playSoundEffect("gameOver");
    }

    public void updateCoinToSQL(int earnedCoin) {
        double newCoin = customer.getCoins() + earnedCoin;

        Connection connectDB = DatabaseConnection.getConnection();

        if (connectDB == null) {
            System.out.println("Something were wrong connectDB is null!");
            return;
        }

        String connectQuery = "UPDATE users SET coin = ? WHERE userID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(connectQuery)) {
            preparedStatement.setDouble(1, newCoin);
            preparedStatement.setInt(2, customer.getUserID());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Coin updated successfully for user ID: " + customer.getUserID());
            } else {
                System.out.println("No rows updated. Check if the user ID exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}

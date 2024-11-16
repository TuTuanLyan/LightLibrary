package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Game.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private Game game;

    public void initialize() {
        game = new Game();
        game.setController(this);

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
            case SPACE -> game.jump();
            case ESCAPE -> togglePause();
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !isPaused && !isGameOver) {
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
        }
    }

    @FXML
    private void restartGame() {
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
            storyBox.setVisible(false);
            isPaused = false;
            game.setPaused(isPaused);
            gameCanvas.setMouseTransparent(isPaused);
        }
    }

    @FXML
    private void handleResumeButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isPaused && !isGameOver) {
            togglePause();
        }
    }

    @FXML
    private void handleRestartButtonClick(MouseEvent event) {
        togglePause();
        restartGame();
    }

    @FXML
    private void handleRetryButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isGameOver) {
            restartGame();
        }
    }

    @FXML
    private void handleReturnButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && (isGameOver || isPaused)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lightlibrary/Views/UserDashboard.fxml"));
                Parent dashboardRoot = loader.load();

                Stage stage = (Stage) gameCanvas.getScene().getWindow();

                Scene dashboardScene = new Scene(dashboardRoot);
                stage.setScene(dashboardScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
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
    }
}

package com.lightlibrary.Controllers;

import com.lightlibrary.Models.Game.Game;
import javafx.fxml.FXML;
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

public class GameController {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Label scoreLabel;
    @FXML
    private ImageView heart1, heart2, heart3;
    @FXML
    private VBox pauseMenu, gameOverMenu;

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private Game game;

    public void initialize() {
        // Khởi tạo trò chơi
        game = new Game();
        game.setController(this);

        // Lấy GraphicsContext để vẽ trò chơi
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // Khởi động trò chơi
        game.startGame(gc);

        // Thiết lập sự kiện bàn phím và chuột
        setupKeyAndMouseListeners();
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
        }
    }

    @FXML
    private void restartGame() {
        isGameOver = false;
        isPaused = false;
        gameOverMenu.setVisible(false);
        pauseMenu.setVisible(false);
        game.restart();
    }

    @FXML
    private void handleResumeButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isPaused && !isGameOver) {
            togglePause();
        }
    }

    @FXML
    private void handleRestartButtonClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && isGameOver) {
            restartGame();
        }
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateHealth(int health) {
        heart1.setVisible(health > 0);
        heart2.setVisible(health > 1);
        heart3.setVisible(health > 2);
    }

    public void showGameOver() {
        isGameOver = true;
        gameOverMenu.setVisible(true);
        gameCanvas.setMouseTransparent(true);
    }
}

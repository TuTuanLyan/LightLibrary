package com.lightlibrary.Models.Game;

import com.lightlibrary.Controllers.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int WINDOW_WIDTH = 960;
    public static final int WINDOW_HEIGHT = 640;
    public static final int GROUND_Y = 500;

    private Robot robot;
    private List<Obstacle> obstacles = new ArrayList<>();
    private int score = 0;
    private int playerHealth = 3;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private AnimationTimer gameLoop;
    private GameController controller;

    public Game() {
        // Khởi tạo nhân vật và chướng ngại vật
        robot = new Robot(100, GROUND_Y - 40, 40, 40);
        obstacles.add(new Motorcycle(WINDOW_WIDTH, GROUND_Y - 30));
        obstacles.add(new Car(WINDOW_WIDTH + 200, GROUND_Y - 30));
        obstacles.add(new Helicopter(WINDOW_WIDTH + 400, GROUND_Y - 160));
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void startGame(GraphicsContext gc) {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused && !isGameOver) {
                    update();
                    render(gc);
                }
            }
        };
        gameLoop.start();
    }

    public void jump() {
        if (!isPaused && !isGameOver) {
            robot.jump();
        }
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public void restart() {
        isGameOver = false;
        score = 0;
        playerHealth = 3;
        robot = new Robot(100, GROUND_Y - 40, 40, 40);
        obstacles.clear();
        obstacles.add(new Motorcycle(WINDOW_WIDTH, GROUND_Y - 30));
        obstacles.add(new Car(WINDOW_WIDTH + 200, GROUND_Y - 30));
        obstacles.add(new Helicopter(WINDOW_WIDTH + 400, GROUND_Y - 160));
        controller.updateScore(score);
        controller.updateHealth(playerHealth);
        gameLoop.start();
    }

    private void update() {
        robot.update();

        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            if (obstacle.x + obstacle.width < 0) {
                obstacle.x = WINDOW_WIDTH + Math.random() * 200;
                score++;
                controller.updateScore(score);
            }
            if (robot.isColliding(obstacle)) {
                playerHealth--;
                controller.updateHealth(playerHealth);
                if (playerHealth <= 0) {
                    gameOver();
                    break;
                }
                // Reset vị trí chướng ngại vật khi có va chạm
                obstacle.x = WINDOW_WIDTH + Math.random() * 200;
                break;
            }
        }
    }

    private void render(GraphicsContext gc) {
        // Xóa canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Vẽ robot và chướng ngại vật
        robot.render(gc);
        for (Obstacle obstacle : obstacles) {
            obstacle.render(gc);
        }

        // Vẽ mặt đất
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(0, GROUND_Y, WINDOW_WIDTH, GROUND_Y);
    }

    private void gameOver() {
        isGameOver = true;
        gameLoop.stop();
        controller.showGameOver();
    }
}

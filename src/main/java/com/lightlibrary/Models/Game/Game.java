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
    public static final int GROUND_Y = 550;

    private Robot robot;
    private int playerHealth = 3;
    private int playerArmor = 0;

    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Background> backgrounds = new ArrayList<>();
    private List<Collectable> collectables = new ArrayList<>();

    private int score = 0;
    private int collectedCoin = 0;

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private AnimationTimer gameLoop;
    private GameController controller;

    public Game() {
        robot = new Robot(100, GROUND_Y - 60, 60, 60);

        collectables.add(new Coin(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));
        collectables.add(new Heart(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));
        collectables.add(new Armor(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));

        obstacles.add(new Motorcycle(WINDOW_WIDTH, GROUND_Y - 60));
        obstacles.add(new Car(WINDOW_WIDTH + 200, GROUND_Y - 60));
        obstacles.add(new Helicopter(WINDOW_WIDTH + 400, GROUND_Y - 250));

        backgrounds.add(new Background(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT));
        backgrounds.add(new Background(WINDOW_WIDTH, 0, WINDOW_WIDTH, WINDOW_HEIGHT));
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
        System.out.println(playerArmor);
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
        collectedCoin = 0;
        playerHealth = 3;
        playerArmor = 0;

        robot = new Robot(100, GROUND_Y - 60, 60, 60);

        collectables.clear();
        collectables.add(new Coin(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));
        collectables.add(new Heart(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));
        collectables.add(new Armor(WINDOW_WIDTH + Math.random() * WINDOW_WIDTH, GROUND_Y - 30 - Math.random() * 150));

        obstacles.clear();
        obstacles.add(new Motorcycle(WINDOW_WIDTH, GROUND_Y - 60));
        obstacles.add(new Car(WINDOW_WIDTH + 200, GROUND_Y - 60));
        obstacles.add(new Helicopter(WINDOW_WIDTH + 400, GROUND_Y - 250));

        controller.updateScore(score);
        controller.updateCollectedCoin(collectedCoin);
        controller.updateHealth(playerHealth);
        controller.updateArmor(playerArmor);
        gameLoop.start();
    }

    private void update() {
        robot.update();
        controller.updateHealth(playerHealth);
        controller.updateArmor(playerArmor);

        for (Background background : backgrounds) {
            background.update();
        }

        for (Collectable collectable : collectables) {
            collectable.update();
            if (collectable.x + collectable.width < 0) {
                collectable.x = WINDOW_WIDTH + Math.random() * WINDOW_WIDTH;
                collectable.y = GROUND_Y - 30 - Math.random() * 150;
            }
            if (robot.isColliding(collectable)) {
                if (collectable instanceof Coin) {
                   collectedCoin++;
                   controller.updateCollectedCoin(collectedCoin);
                } else if (collectable instanceof Heart) {
                    playerHealth = Math.min(playerHealth + 1, 3);
                    controller.updateHealth(playerHealth);
                } else {
                    // armor part
                    playerArmor = Math.min(playerArmor + 1, 3);
                    controller.updateArmor(playerArmor);
                }
                collectable.x = WINDOW_WIDTH + Math.random() * WINDOW_WIDTH;
                collectable.y = GROUND_Y - 30 - Math.random() * 150;
            }
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            if (obstacle.x + obstacle.width < 0) {
                obstacle.x = WINDOW_WIDTH + Math.random() * 200;
                score++;
                controller.updateScore(score);
            }
            if (robot.isColliding(obstacle)) {
                if (playerArmor > 0) {
                    playerArmor--;
                    controller.updateArmor(playerArmor);
                } else {
                    playerHealth--;
                    controller.updateHealth(playerHealth);
                }
                if (playerHealth <= 0) {
                    gameOver();
                    break;
                }
                obstacle.x = WINDOW_WIDTH + Math.random() * 200;
                break;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (Background background : backgrounds) {
            background.render(gc);
        }

        for (Collectable collectable : collectables) {
            collectable.render(gc);
        }

        robot.render(gc);

        for (Obstacle obstacle : obstacles) {
            obstacle.render(gc);
        }

        // Vẽ mặt đất
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeLine(0, GROUND_Y, WINDOW_WIDTH, GROUND_Y);
    }

    private void gameOver() {
        isGameOver = true;
        collectedCoin += score;
        controller.updateCollectedCoin(collectedCoin);
        controller.updateCoinToSQL(collectedCoin);
        gameLoop.stop();
        controller.showGameOver();
    }
}

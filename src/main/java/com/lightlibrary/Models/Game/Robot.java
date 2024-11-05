package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Robot extends GameObject {
    private boolean isJumping = false;
    private double jumpVelocity = 0;
    private double gravity = 0.5;

    public Robot(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = -10; // Tốc độ nhảy ban đầu
        }
    }

    @Override
    public void update() {
        if (isJumping) {
            y += jumpVelocity;
            jumpVelocity += gravity;

            if (y >= Game.GROUND_Y - 40) { // Giả sử 300 là mặt đất
                y = Game.GROUND_Y - 40;
                isJumping = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, width, height);
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public double getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(double jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }
}

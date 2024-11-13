package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Robot extends GameObject {
    private boolean isJumping = false;
    private double jumpVelocity = 0;
    private double gravity = 0.5;
    private Image robotImage;

    public Robot(double x, double y, double width, double height) {
        super(x, y, width, height);
        robotImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/robot.jpg")).toExternalForm());
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = -12; // Tốc độ nhảy ban đầu
        }
    }

    @Override
    public void update() {
        if (isJumping) {
            y += jumpVelocity;
            jumpVelocity += gravity;

            if (y >= Game.GROUND_Y - 60) {
                y = Game.GROUND_Y - 60;
                isJumping = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (robotImage != null) {
            gc.drawImage(robotImage, x, y, width, height); // Vẽ ảnh robot
        } else {
            gc.setFill(Color.CORNFLOWERBLUE); // Màu dự phòng nếu ảnh không tải được
            gc.fillRect(x, y, width, height);
        }
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

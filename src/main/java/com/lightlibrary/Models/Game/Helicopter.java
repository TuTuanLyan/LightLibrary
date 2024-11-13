package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Helicopter extends Obstacle {
    private Image helicopterImage;

    public Helicopter(double x, double y) {
        super(x, y, 120, 60, 3);
        helicopterImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/helicopter.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (helicopterImage != null) {
            gc.drawImage(helicopterImage, x, y, width, height);
        } else {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(x, y, width, height);
        }
    }
}

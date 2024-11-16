package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Helicopter extends Obstacle {
    private Image helicopterImage;

    public Helicopter(double x, double y) {
        super(x, y, 120, 60, 3);
        helicopterImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/helicopter.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(helicopterImage, x, y, width, height);
    }
}

package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Motorcycle extends Obstacle {
    private Image motorcycleImage;

    public Motorcycle(double x, double y) {
        super(x, y, 60, 60, 5);
        motorcycleImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/motorcycle.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(motorcycleImage, x, y, width, height);
    }
}
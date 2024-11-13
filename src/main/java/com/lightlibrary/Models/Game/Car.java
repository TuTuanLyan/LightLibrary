package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Car extends Obstacle {
    private Image carImage;

    public Car(double x, double y) {
        super(x, y, 90, 60, 4);
        carImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/car.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        if (carImage != null) {
            gc.drawImage(carImage, x, y, width, height);
        } else {
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y, width, height);
        }
    }
}
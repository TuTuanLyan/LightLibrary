package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Car extends Obstacle {
    private Image carImage;

    public Car(double x, double y) {
        super(x, y, 90, 60, 4);
        carImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/car.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(carImage, x, y, width, height);
    }
}

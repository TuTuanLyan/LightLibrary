package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Car extends Obstacle {
    public Car(double x, double y) {
        super(x, y, 20, 40, 4);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y, width, height);
    }
}

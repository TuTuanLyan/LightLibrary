package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Motorcycle extends Obstacle {
    public Motorcycle(double x, double y) {
        super(x, y, 50, 30, 5);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
    }
}
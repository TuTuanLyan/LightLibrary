package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Helicopter extends Obstacle {
    public Helicopter(double x, double y) {
        super(x, y, 60, 30, 3);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x, y, width, height);
    }
}

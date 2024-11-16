package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Heart extends Collectable {
    private Image heartImage;

    public Heart(double x, double y) {
        super(x, y, 30, 30, 3);
        heartImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/heart.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(heartImage, x, y, width, height);
    }
}

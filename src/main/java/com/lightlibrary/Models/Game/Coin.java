package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Coin extends Collectable {
    private Image coinImage;

    public Coin(double x, double y) {
        super(x, y, 30, 30, 3);
        coinImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/coin.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(coinImage, x, y, width, height);
    }
}

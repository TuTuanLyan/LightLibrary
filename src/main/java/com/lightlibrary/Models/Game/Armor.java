package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Armor extends Collectable {
    private Image armorImage;

    public Armor(double x, double y) {
        super(x, y, 30, 30, 3);
        armorImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/armor.jpg")).toExternalForm());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(armorImage, x, y, width, height);
    }
}

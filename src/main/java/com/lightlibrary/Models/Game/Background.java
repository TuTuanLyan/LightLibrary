package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Background extends GameObject{
    private static final int SPEED = 2;
    private Image backgroundImage;

    public Background(int x, int y, int width, int height) {
        super(x, y, width, height);
        backgroundImage = new Image(Objects.requireNonNull(getClass().getResource("/com/lightlibrary/Images/Game/background.jpg")).toExternalForm());
    }

    @Override
    public void update() {
        // Di chuyển background từ phải sang trái
        x -= SPEED;

        // Reset vị trí nếu background ra khỏi màn hình
        if (x + width <= 0) {
            x = Game.WINDOW_WIDTH;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(backgroundImage, x, y, width, height);
    }
}

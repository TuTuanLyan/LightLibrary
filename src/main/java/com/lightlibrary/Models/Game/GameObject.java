package com.lightlibrary.Models.Game;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected double x, y;
    protected double width, height;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GraphicsContext gc);
    public abstract void update();

    public boolean isColliding(GameObject other) {
        // Thu nhỏ hitbox của this
        double thisLeft = this.x + 9;
        double thisRight = this.x + this.width - 9;
        double thisTop = this.y + 9;
        double thisBottom = this.y + this.height - 9;

        // Thu nhỏ hitbox của other
        double otherLeft = other.x + 9;
        double otherRight = other.x + other.width - 9;
        double otherTop = other.y + 9;
        double otherBottom = other.y + other.height - 9;

        return thisLeft < otherRight &&
                thisRight > otherLeft &&
                thisTop < otherBottom &&
                thisBottom > otherTop;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}

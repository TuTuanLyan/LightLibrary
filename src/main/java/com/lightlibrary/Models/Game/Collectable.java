package com.lightlibrary.Models.Game;

public abstract class Collectable extends GameObject {
    protected double speed;

    public Collectable(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    public void move() {
        this.x -= speed;
    }

    public void update() {
        move();
    }
}

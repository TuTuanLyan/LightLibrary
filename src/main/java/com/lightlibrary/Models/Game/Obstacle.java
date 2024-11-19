package com.lightlibrary.Models.Game;

public abstract class Obstacle extends GameObject {
    protected double speed;

    public Obstacle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    public void move() {
        x -= speed;
    }

    @Override
    public void update() {
        move();
    }
}
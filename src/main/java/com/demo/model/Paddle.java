package com.demo.model;

public class Paddle extends Movable{
    private int currentPowerUp;

    public Paddle(double dx, double dy, double x, double y, double width, double height) {
        super(dx, dy, x, y, width, height);
        this.currentPowerUp = 0;
    }
}

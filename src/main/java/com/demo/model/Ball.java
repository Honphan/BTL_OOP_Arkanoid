package com.demo.model;

public class Ball extends Movable{
    private double radius;
    public Ball(double dx, double dy, double x, double y) {
        super(dx, dy, x, y, 20, 20);
        this.radius = 10;
    }

    public void bounceOffWall(){}
    public void checkCollision(){}

}

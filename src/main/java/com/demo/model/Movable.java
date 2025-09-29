package com.demo.model;

public class Movable extends GameObject {
    public double dx;
    public double dy;

    public Movable(double dx, double dy, double x, double y,double width, double height) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    public void move(){}

}
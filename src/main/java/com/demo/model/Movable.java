package com.demo.model;

public abstract class Movable extends GameObject {
    protected double dx;
    protected double dy;

    public Movable(double dx, double dy, double x, double y,double width, double height) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    public  abstract void move();

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}
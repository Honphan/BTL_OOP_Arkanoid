package com.demo.model;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Rectangle node;



    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Node getNode() {
        return node;
    }

    public abstract void update();


    public abstract void render(GraphicsContext gc);
}

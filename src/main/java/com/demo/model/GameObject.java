package com.demo.model;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public class GameObject {
    public double x;
    public double y;
    public double width;
    public double height;
    public Rectangle node;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Node getNode() {
        return node;
    }

    public void update() {

    }

    public void render() {

    }
}

package com.demo.model;

public class Brick extends GameObject{
    private int hitPoints;
    private int type;

    public Brick(int x, int y, int width, int height, int hitPoints, int type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }

    public void takeHit(){}
    public void isDestroyed(){}

}

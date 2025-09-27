package com.demo.model;

public class PowerUp extends GameObject {
    private int type;
    private int duration;

    public PowerUp(int x, int y, int width, int height, int type, int duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    public void applyPowerUp(){}
    public void removePowerUp(){}

}

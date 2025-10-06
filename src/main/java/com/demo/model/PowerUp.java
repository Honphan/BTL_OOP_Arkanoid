package com.demo.model;

import javafx.scene.canvas.GraphicsContext;

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

    @Override
    public void update() {

    }

    @Override
    public void render(GraphicsContext gc) {

    }
}

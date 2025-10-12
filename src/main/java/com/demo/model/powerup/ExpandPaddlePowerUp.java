package com.demo.model.powerup;

import com.demo.model.GameObject;
import com.demo.model.Movable;
import com.demo.model.Paddle;
import javafx.scene.image.Image;

/**
 * Paddle rộng ra.
 */
public class ExpandPaddlePowerUp extends PowerUp {

    private double expandAmount;

    public ExpandPaddlePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height, PowerUpType.EXPAND_PADDLE, 8000, velocityY,
                new Image(ExpandPaddlePowerUp.class.getResourceAsStream("/com/demo/view/image/powerup/expandpaddle.png")));

        this.expandAmount = 2;
    }

    @Override
    public void applyPowerUp(Movable target) {
    }

    @Override
    public void removePowerUp(Movable target) {}
}

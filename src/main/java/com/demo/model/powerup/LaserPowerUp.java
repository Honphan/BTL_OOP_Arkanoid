package com.demo.model.powerup;

import com.demo.model.Movable;
import com.demo.model.Paddle;
import javafx.scene.image.Image;

/**
 * Power-up cho phép paddle bắn laser phá gạch phía trên.
 */
public class LaserPowerUp extends PowerUp {

    public LaserPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.LASER,
                velocityY,
                new Image(LaserPowerUp.class.getResource("/com/demo/view/image/powerups/laser.png").toExternalForm(), true));
    }

    @Override
    public void applyPowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.setLaserEnabled(true);
        }
    }

    @Override
    public void removePowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.setLaserEnabled(false);
        }
    }
}

package com.demo.model.powerup;

import com.demo.model.Movable;
import javafx.scene.image.Image;

/**
 * Thêm mạng.
 */
public class ExtraLifePowerUp extends PowerUp {

    public ExtraLifePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height, PowerUpType.EXTRA_LIFE, 8000, velocityY,
                new Image(ExtraLifePowerUp.class.getResourceAsStream("/com/demo/view/image/powerup/extralife.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
    }

    @Override
    public void removePowerUp(Movable target) {}
}

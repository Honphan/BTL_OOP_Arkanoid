package com.demo.model.powerup;

import com.demo.model.Ball;
import com.demo.model.Movable;
import javafx.scene.image.Image;
import java.util.List;

/**
 * Bóng đi xuyên qua gạch.
 */
public class PiercingBallPowerUp extends PowerUp {

    public PiercingBallPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height, PowerUpType.PIERCING_BALL, getDefaultDuration(), velocityY,
                new Image(PiercingBallPowerUp.class.getResourceAsStream("/com/demo/view/image/powerups/piercingball.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
    }

    @Override
    public void removePowerUp(Movable target) {}

}

package com.demo.model.powerup;

import com.demo.model.Ball;
import com.demo.model.Movable;
import javafx.scene.image.Image;
import java.util.List;

/**
 * Nhân đôi bóng.
 */
public class MultiBallPowerUp extends PowerUp {

    public MultiBallPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height, PowerUpType.MULTI_BALL, 8000, velocityY,
                new Image(MultiBallPowerUp.class.getResourceAsStream("/com/demo/view/image/powerup/multiball.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
    }

    @Override
    public void removePowerUp(Movable target) {}
}

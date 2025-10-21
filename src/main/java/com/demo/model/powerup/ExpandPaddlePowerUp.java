package com.demo.model.powerup;

import com.demo.model.GameObject;
import com.demo.model.Movable;
import com.demo.model.Paddle;
import javafx.scene.image.Image;

/**
 * Paddle rộng ra.
 */
public class ExpandPaddlePowerUp extends PowerUp {

    public ExpandPaddlePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height, PowerUpType.EXPAND_PADDLE, PowerUp.getDefaultDuration(), velocityY,
                new Image(ExpandPaddlePowerUp.class.getResourceAsStream("/com/demo/view/image/powerup/expandpaddle.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
        if (target instanceof Paddle) {
            Paddle paddle = (Paddle) target;
            // x2 paddle
            paddle.expandPaddle(2);
        }
    }

    @Override
    public void removePowerUp(Movable target) {
        if (target instanceof Paddle) {
            Paddle paddle = (Paddle) target;
            // Reset về kích thước gốc
            paddle.resetPaddleSize();
        }
    }
}

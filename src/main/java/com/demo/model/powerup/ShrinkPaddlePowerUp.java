package com.demo.model.powerup;

import com.demo.model.Movable;
import com.demo.model.Paddle;
import javafx.scene.image.Image;

/**
 * PowerUp giúp paddle thu nhỏ trong thời gian giới hạn.
 */
public class ShrinkPaddlePowerUp extends PowerUp {

    public ShrinkPaddlePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.SHRINK_PADDLE,
                getDefaultDuration(),
                velocityY,
                new Image(ShrinkPaddlePowerUp.class.getResourceAsStream("/com/demo/view/image/powerups/shrinkpaddle.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.shrinkPaddle(0.5); // Giảm kích thước còn một nửa
        }
    }

    @Override
    public void removePowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.resetPaddleSize(); // Trả về kích thước ban đầu
        }
    }
}

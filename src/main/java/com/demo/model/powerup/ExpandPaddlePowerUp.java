package com.demo.model.powerup;

import com.demo.model.Movable;
import com.demo.model.Paddle;
import javafx.scene.image.Image;

/**
 * PowerUp giúp paddle mở rộng gấp đôi trong thời gian giới hạn.
 */
public class ExpandPaddlePowerUp extends PowerUp {


    public ExpandPaddlePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.EXPAND_PADDLE,
                getDefaultDuration(),
                velocityY,
                new Image(PiercingBallPowerUp.class.getResource("/com/demo/view/image/powerups/expandpaddle.png").toExternalForm(), true));
    }

    @Override
    public void applyPowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.expandPaddle(1.5); // Mở rộng gấp đôi
        }
    }

    @Override
    public void removePowerUp(Movable target) {
        if (target instanceof Paddle paddle) {
            paddle.resetPaddleSize(); // Trả về kích thước ban đầu
        }
    }
}

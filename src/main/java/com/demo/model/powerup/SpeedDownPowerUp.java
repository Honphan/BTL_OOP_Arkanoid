package com.demo.model.powerup;

import com.demo.model.Ball;
import com.demo.model.Movable;
import javafx.scene.image.Image;

import java.util.List;

/**
 * PowerUp giảm tốc bóng trong thời gian giới hạn.
 */
public class SpeedDownPowerUp extends PowerUp {

    public SpeedDownPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.SPEED_DOWN,
                getDefaultDuration(),
                velocityY,
                new Image(SpeedDownPowerUp.class.getResourceAsStream("/com/demo/view/image/powerups/speeddown.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {

    }

    @Override
    public void applyPowerUp(Movable target, List<Ball> balls) {
        for (Ball ball : balls) {
            ball.setSpeed(ball.getSpeed() * 0.6); // giảm 40%
        }
    }

    @Override
    public void removePowerUp(Movable target) {

    }

    @Override
    public void removePowerUp(Movable target, List<Ball> balls) {
        for (Ball ball : balls) {
            ball.resetSpeed(); // khôi phục tốc độ gốc
        }
    }
}

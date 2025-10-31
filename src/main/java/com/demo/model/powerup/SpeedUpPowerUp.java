package com.demo.model.powerup;

import com.demo.model.Ball;
import com.demo.model.Movable;
import com.demo.model.Paddle;
import com.demo.model.brick.PowerUpBrick;
import javafx.scene.image.Image;

import java.util.List;

/**
 * PowerUp tăng tốc bóng trong thời gian giới hạn.
 */
public class SpeedUpPowerUp extends PowerUp {

    public SpeedUpPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.SPEED_UP,
                getDefaultDuration(),
                velocityY,
                new Image(SpeedUpPowerUp.class.getResourceAsStream("/com/demo/view/image/powerups/speedup.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {

    }

    @Override
    public void applyPowerUp(Movable target, List<Ball> balls) {
        for (Ball ball : balls) {
            double boostedSpeed = ball.getSpeed() * 1.5;
            ball.setSpeed(boostedSpeed, getDuration());
        }
    }

    @Override
    public void removePowerUp(Movable target) {

    }

    @Override
    public void removePowerUp(Movable target, List<Ball> balls) {
        for (Ball ball : balls) {
            ball.resetSpeed(); // trả về tốc độ gốc
        }
    }

}

package com.demo.model.powerup;

import com.demo.model.Movable;
import javafx.scene.image.Image;

/**
 * PowerUp thêm mạng — hiệu ứng tức thời, xử lý trong controller.
 */
public class ExtraLifePowerUp extends PowerUp {

    private static final String IMAGE_PATH = "/com/demo/view/image/powerup/extralife.png";

    public ExtraLifePowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.EXTRA_LIFE,
                0, // hiệu ứng tức thời
                velocityY,
                new Image(PiercingBallPowerUp.class.getResource("/com/demo/view/image/powerups/extralife.png").toExternalForm(), true));
    }


    @Override
    public void applyPowerUp(Movable target) {
        // Không làm gì ở đây — controller sẽ xử lý tăng mạng
    }

    @Override
    public void removePowerUp(Movable target) {
        // Không cần gỡ hiệu ứng
    }
}

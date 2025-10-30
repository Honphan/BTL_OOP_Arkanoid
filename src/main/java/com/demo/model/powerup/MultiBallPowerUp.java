package com.demo.model.powerup;

import com.demo.model.Movable;
import javafx.scene.image.Image;

/**
 * PowerUp nhân đôi bóng — hiệu ứng tức thời, bóng được tạo ở PowerUpManager.
 */
public class MultiBallPowerUp extends PowerUp {

    public MultiBallPowerUp(double x, double y, double width, double height, double velocityY) {
        super(x, y, width, height,
                PowerUpType.MULTI_BALL,
                0, // hiệu ứng tức thời, không cần thời gian
                velocityY,
                new Image(MultiBallPowerUp.class.getResourceAsStream("/com/demo/view/image/powerups/multiball.png")));
    }

    @Override
    public void applyPowerUp(Movable target) {
        // Không cần xử lý ở đây — bóng đã được tạo trong PowerUpManager
    }

    @Override
    public void removePowerUp(Movable target) {
        // Không cần xóa bóng — hiệu ứng vĩnh viễn
    }
}

package com.demo.model;

import com.demo.util.Background;
import com.demo.model.powerup.PowerUp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Paddle extends Movable {
    private double speed;
    private PowerUp currentPowerUp;
    private Image paddleImage;
    private Image paddlePowerUpImage; // Hình ảnh khi có power-up
    private double originalWidth;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(0, 0, x, y, width, height);
        this.speed = speed;
        this.currentPowerUp = null;
        this.originalWidth = width;
        loadImages();
    }

    private void loadImages() {
        try {
            // Load hình ảnh paddle gốc
            paddleImage = new Image(getClass().getResourceAsStream("/com/demo/view/image/paddle/paddle.png"));
            paddlePowerUpImage = new Image(getClass().getResourceAsStream("/com/demo/view/image/paddle/powerup.png"));

        } catch (Exception e) {
            System.err.println("Lỗi khi load hình ảnh paddle: " + e.getMessage());
            paddleImage = null;
            paddlePowerUpImage = null;
        }
    }


    public void update(double canvasWidth) {
        x += dx;
        x = Math.max(0, Math.min(x, canvasWidth - width));
    }

    @Override
    public void update() {
        // Không sử dụng trong context này
    }

    @Override
    public void move() {
        // Không sử dụng trong context này
    }

    public void render(GraphicsContext gc) {
        if (paddleImage != null) {
            Image img = (currentPowerUp != null && currentPowerUp.isActivated())
                    ? paddlePowerUpImage : paddleImage;
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(currentPowerUp != null ? Color.GOLD : Color.BLUE);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, width, height);
        }
    }

    // --- Di chuyển ---
    public void moveLeft() {
        dx = -speed;
    }

    public void moveRight() {
        dx = speed;
    }

    public void stop() {
        dx = 0;
    }

    // --- PowerUp ---
    public void applyPowerUp(PowerUp powerUp) {
        if (currentPowerUp != null) {
            removePowerUp();
        }
        currentPowerUp = powerUp;
        powerUp.applyPowerUp(this);
    }

    public void removePowerUp() {
        if (currentPowerUp != null) {
            currentPowerUp.removePowerUp(this);
            currentPowerUp = null;
        }
    }

    // --- Paddle kích thước ---
    public void expandPaddle(double multiplier) {
        width = originalWidth * multiplier;
    }

    public void shrinkPaddle(double multiplier) {
        width = originalWidth * multiplier;
    }

    public void resetPaddleSize() {
        width = originalWidth;
    }

    // --- Getter ---
    public double getSpeed() {
        return speed;
    }

    public PowerUp getCurrentPowerUp() {
        return currentPowerUp;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }

    public double getOriginalWidth() {
        return originalWidth;
    }
}
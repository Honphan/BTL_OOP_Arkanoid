package com.demo.model;

import com.demo.util.Background;
import com.demo.model.powerup.PowerUp;
import com.demo.model.brick.Brick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class Paddle extends Movable {
    private double speed;
    private PowerUp currentPowerUp;
    private Image paddleImage;
    private double originalWidth;

    private boolean laserEnabled = false;
    private long lastLaserShotTime = 0;
    private static final long LASER_COOLDOWN = 300; // thời gian giữa các lần bắn (ms)


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
            paddleImage = new Image(getClass().getResource("/com/demo/view/image/paddles/56-Breakout-Tiles.png").toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("Lỗi khi load hình ảnh paddle: " + e.getMessage());
            paddleImage = null;
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
        // Vẽ paddle
        if (paddleImage != null) {
            gc.drawImage(paddleImage, x, y, width, height);
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
            currentPowerUp.setConsumed(true);
            currentPowerUp = null;
        }
    }

    public boolean canShootLaser() {
        if (!laserEnabled) return false;
        long now = System.currentTimeMillis();
        return now - lastLaserShotTime >= LASER_COOLDOWN;
    }

    public void markLaserShot() {
        lastLaserShotTime = System.currentTimeMillis();
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

    public void setLaserEnabled(boolean enabled) {
        this.laserEnabled = enabled;
    }

    public boolean isLaserEnabled() {
        return laserEnabled;
    }

    public long getLastLaserShotTime() {
        return lastLaserShotTime;
    }

    public void setLastLaserShotTime(long time) {
        this.lastLaserShotTime = time;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setCurrentPowerUp(PowerUp currentPowerUp) {
        this.currentPowerUp = currentPowerUp;
    }

    public Image getPaddleImage() {
        return paddleImage;
    }

    public void setPaddleImage(Image paddleImage) {
        this.paddleImage = paddleImage;
    }

    public void setOriginalWidth(double originalWidth) {
        this.originalWidth = originalWidth;
    }
}
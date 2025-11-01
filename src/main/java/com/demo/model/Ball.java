package com.demo.model;

import com.demo.model.brick.Brick;
import com.demo.util.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Lớp Ball đại diện cho quả bóng trong game.
 * Xử lý di chuyển, va chạm, phản xạ, và âm thanh.
 */
public class Ball extends Movable {

    // Tốc độ và hướng bay
    private double speed;
    private double directionX;
    private double directionY;

    // Kích thước bóng
    private final int radius = 10;
    private final double originalSpeed;

    // Ảnh bóng thường
    private Image ballImage;

    // Trạng thái xuyên gạch
    private boolean isPiercing = false;
    private long piercingEndTime = 0;

    // Trạng thái tăng tốc
    private long speedBoostEndTime = 0;

    // Quản lý âm thanh
    private final SoundManager soundManager;

    // Khởi tạo bóng
    public Ball(double x, double y, double speed) {
        super(0, 0, x, y, 20, 20);
        this.speed = speed;
        this.originalSpeed = speed;
        this.directionX = 0.8;
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();
        updateVelocity();

        try {
            ballImage = new Image(getClass().getResource("/com/demo/view/image/balls/58-Breakout-Tiles.png").toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("Không load được ảnh bóng: " + e.getMessage());
        }
    }

    // Tính dx, dy từ hướng bay và tốc độ
    private void updateVelocity() {
        double mag = Math.sqrt(directionX * directionX + directionY * directionY);
        if (mag > 0) {
            this.dx = (directionX / mag) * speed;
            this.dy = (directionY / mag) * speed;
        } else {
            this.dx = 0;
            this.dy = 0;
        }
    }

    /**
     * Cập nhật hướng bay từ vận tốc hiện tại.
     */
    private void updateDirectionFromVelocity() {
        double mag = Math.sqrt(dx * dx + dy * dy);
        if (mag > 0) {
            directionX = dx / mag;
            directionY = dy / mag;
        }
    }

    // Reset về tốc độ gốc
    public void resetSpeed() {
        this.speed = originalSpeed;
        updateVelocity();
    }

    // Di chuyển bóng
    @Override
    public void move() {
        x += dx;
        y += dy;
    }

    // Cập nhật trạng thái bóng mỗi frame
    @Override
    public void update() {
        move();
        updateSpeedBoostStatus();
        updatePiercingStatus();
    }

    // Vẽ bóng lên canvas
    public void render(GraphicsContext gc) {
        if (isPiercing) {
            gc.drawImage(ballImage, x, y, radius, radius);
        } else if (ballImage != null) {
            gc.drawImage(ballImage, x, y, radius, radius);
        }
    }

    // --- Va chạm với hình chữ nhật (gạch, paddle) ---
    public boolean checkCollision(GameObject other) {
        double ballCenterX = this.x + radius;
        double ballCenterY = this.y + radius;

        double closestX = Math.max(other.getX(), Math.min(ballCenterX, other.getX() + other.getWidth()));
        double closestY = Math.max(other.getY(), Math.min(ballCenterY, other.getY() + other.getHeight()));

        double distanceX = ballCenterX - closestX;
        double distanceY = ballCenterY - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        return distanceSquared <= (radius * radius);
    }

    // --- Va chạm với bóng khác ---
    public boolean checkCollisionWithBall(Ball other) {
        double dx = this.getCenterX() - other.getCenterX();
        double dy = this.getCenterY() - other.getCenterY();
        double distanceSquared = dx * dx + dy * dy;
        return distanceSquared <= (this.radius + other.radius) * (this.radius + other.radius);
    }

    // --- Phản xạ ---
    private void bounceHorizontally() {
        directionX *= -1;
        updateVelocity();
    }

    private void bounceVertically() {
        directionY *= -1;
        updateVelocity();
    }

    /**
     * Phản xạ khi va chạm với hình chữ nhật (gạch, paddle).
     */
    public void bounceOff(GameObject other) {
        double ballCenterX = getCenterX();
        double ballCenterY = getCenterY();

        double closestX = Math.max(other.getX(), Math.min(ballCenterX, other.getX() + other.getWidth()));
        double closestY = Math.max(other.getY(), Math.min(ballCenterY, other.getY() + other.getHeight()));

        double normalX = ballCenterX - closestX;
        double normalY = ballCenterY - closestY;
        double magnitude = Math.sqrt(normalX * normalX + normalY * normalY);

        if (magnitude == 0) {
            // Nếu tâm bóng nằm đúng trên cạnh gạch
            normalX = -directionX;
            normalY = -directionY;
            magnitude = Math.sqrt(normalX * normalX + normalY * normalY);
        }

        normalX /= magnitude;
        normalY /= magnitude;

        // Phản xạ theo công thức vật lý
        double dot = dx * normalX + dy * normalY;
        dx = dx - 2 * dot * normalX;
        dy = dy - 2 * dot * normalY;

        updateDirectionFromVelocity();

        // Đẩy bóng ra khỏi vùng va chạm
        x += normalX * radius * 0.5;
        y += normalY * radius * 0.5;

        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
    }

    /**
     * Phản xạ khi va chạm với paddle — tính góc bật theo vị trí va chạm.
     */
    public void bounceOffPaddle(GameObject paddle) {
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
        double ballCenterX = this.getCenterX();
        double hitPosition = (ballCenterX - paddleCenterX) / (paddle.getWidth() / 2);
        hitPosition = Math.max(-1, Math.min(1, hitPosition));

        double angle = hitPosition * Math.toRadians(75);
        directionX = Math.sin(angle);
        directionY = -Math.cos(angle);

        updateVelocity();
        this.y = paddle.getY() - 2 * radius;
        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
    }

    /**
     * Phản xạ khi va chạm với bóng khác.
     */
    public void bounceOffBall(Ball other) {
        double deltaX = this.getCenterX() - other.getCenterX();
        double deltaY = this.getCenterY() - other.getCenterY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance == 0) {
            distance = 0.01;
            deltaX = Math.random() - 0.5;
            deltaY = Math.random() - 0.5;
        }

        double minDistance = this.radius + other.radius;
        if (distance < minDistance) {
            double overlap = minDistance - distance;
            double pushX = (deltaX / distance) * overlap / 2;
            double pushY = (deltaY / distance) * overlap / 2;

            this.x += pushX;
            this.y += pushY;
            other.x -= pushX;
            other.y -= pushY;
        }

        double nx = deltaX / distance;
        double ny = deltaY / distance;

        double v1n = this.dx * nx + this.dy * ny;
        double v2n = other.dx * nx + other.dy * ny;

        if (v1n - v2n >= 0) return;

        double v1nNew = v2n;
        double v2nNew = v1n;

        this.dx += (v1nNew - v1n) * nx;
        this.dy += (v1nNew - v1n) * ny;
        other.dx += (v2nNew - v2n) * nx;
        other.dy += (v2nNew - v2n) * ny;

        updateDirectionFromVelocity();
        other.updateDirectionFromVelocity();
    }

    /**
     * Phản xạ khi chạm tường.
     */
    public void bounceOffWall(double canvasWidth, double canvasHeight) {
        boolean hitWall = false;

        if (this.x <= 0) {
            this.x = 0;
            bounceHorizontally();
            hitWall = true;
        } else if (this.x + 2 * radius >= canvasWidth) {
            this.x = canvasWidth - 2 * radius;
            bounceHorizontally();
            hitWall = true;
        }

        if (this.y <= 0) {
            this.y = 0;
            bounceVertically();
            hitWall = true;
        }

        if (hitWall) {
            soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
        }
    }

    // --- Trạng thái & tiện ích ---
    public boolean isOutOfBounds(double canvasHeight) {
        return this.y > canvasHeight;
    }

    // Reset vị trí và hướng bóng
    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.directionX = 1;
        this.directionY = -1;
        updateVelocity();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double newSpeed, long durationMillis) {
        if (System.currentTimeMillis() < speedBoostEndTime) {
            // Đang boost → chỉ gia hạn thời gian nếu cần
            speedBoostEndTime = Math.max(speedBoostEndTime, System.currentTimeMillis() + durationMillis);
        } else {
            // Chưa boost → áp dụng boost mới
            this.speed = newSpeed;
            updateVelocity();
            speedBoostEndTime = System.currentTimeMillis() + durationMillis;
        }
    }


    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public int getRadius() {
        return radius;
    }

    public double getCenterX() {
        return x + radius;
    }

    public double getCenterY() {
        return y + radius;
    }

    /**
     * set piercing time
     */
    public void setPiercing(boolean piercing, long duration) {
        this.isPiercing = piercing;
        if (piercing) {
            this.piercingEndTime = System.currentTimeMillis() + duration;
        } else {
            this.piercingEndTime = 0;
        }
    }

    /**
     * cập nhật trạng thái piercing
     */
    public void updatePiercingStatus() {
        if (isPiercing && System.currentTimeMillis() >= piercingEndTime) {
            isPiercing = false;
            piercingEndTime = 0;
        }
    }

    private void updateSpeedBoostStatus() {
        if (speedBoostEndTime > 0 && System.currentTimeMillis() >= speedBoostEndTime) {
            resetSpeed(); // trở về tốc độ gốc
            speedBoostEndTime = 0;
        }
    }

    public boolean isPiercing() {
        return isPiercing;
    }
}
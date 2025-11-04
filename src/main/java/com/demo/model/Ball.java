package com.demo.model;

import com.demo.model.brick.Brick;
import com.demo.util.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Lớp Ball đại diện cho quả bóng trong game.
 */
public class Ball extends Movable {

    // Tốc độ và hướng bay
    private double speed;
    private double directionX;
    private double directionY;

    // Kích thước bóng
    private int radius = 10;
    private final double originalSpeed;

    // Ảnh bóng thường
    private Image ballImage;
    private Image ballPUImage;

    // Trạng thái xuyên gạch
    private boolean isPiercing = false;
    private long piercingEndTime = 0;

    // Trạng thái tăng tốc
    private long lastSpeedEffectTime = 0;
    private long speedUpEndTime = 0;
    private long speedDownEndTime = 0;
    private boolean isSpeedUpActive = false;
    private boolean isSpeedDownActive = false;
    private boolean ballReady = false;



    // Quản lý âm thanh
    private final SoundManager soundManager;

    // Khởi tạo bóng
    public Ball(double x, double y, double speed) {
        super(0, 0, x, y, 20, 20);
        this.speed = speed;
        this.originalSpeed = speed;
        this.directionX = 0.2;
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();

        //Chuẩn hóa direction thành vector đơn vị
        normalizeDirection();

        updateVelocity();

        try {

            ballImage = new Image(getClass().getResource("/com/demo/view/image/balls/58-Breakout-Tiles.png").toExternalForm(), true);
            ballPUImage = new Image(getClass().getResource("/com/demo/view/image/balls/icon_512x512.png").toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("Không load được ảnh bóng: " + e.getMessage());
        }
    }

    //  Chuẩn hóa directionX/Y thành vector đơn vị ---
    public void normalizeDirection() {
        double mag = Math.sqrt(directionX * directionX + directionY * directionY);
        if (mag > 0) {
            directionX /= mag;
            directionY /= mag;
        } else {
            directionX = 0.8;
            directionY = -1;
            normalizeDirection();
        }
    }

    // Tính dx, dy từ hướng bay và tốc độ
    public void updateVelocity() {
        this.dx = directionX * speed;
        this.dy = directionY * speed;
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
        if (!ballReady) return;
        move();
        updateSpeedBoostStatus();
        updatePiercingStatus();
    }

    public void render(GraphicsContext gc) {
        if (isPiercing && ballPUImage != null) {
            gc.drawImage(ballPUImage, x, y, radius * 2, radius * 2);
        } else if (ballImage != null) {
            gc.drawImage(ballImage, x, y, radius * 2, radius * 2);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, radius * 2, radius * 2);
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
            normalX = -directionX;
            normalY = -directionY;
            magnitude = Math.sqrt(normalX * normalX + normalY * normalY);
            if (magnitude == 0) return;
        }

        normalX /= magnitude;
        normalY /= magnitude;

        // Phản xạ theo công thức vật lý
        double dot = dx * normalX + dy * normalY;
        dx = dx - 2 * dot * normalX;
        dy = dy - 2 * dot * normalY;

        // cập nhật direction từ dx, dy
        updateDirectionFromVelocity();

        // Đẩy bóng ra khỏi vùng va chạm
        double pushDistance = radius - magnitude + 1; // +1 để tránh kẹt
        x += normalX * pushDistance;
        y += normalY * pushDistance;

        //Giữ tốc độ ổn định
        updateVelocity();

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

        // đảm bảo direction là vector đơn vị
        normalizeDirection();

        updateVelocity();
        this.y = paddle.getY() - 2 * radius - 1; // +1 tránh kẹt
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

        // Cập nhật direction cho cả hai
        updateDirectionFromVelocity();
        other.updateDirectionFromVelocity();

        // Giữ tốc độ ổn định
        updateVelocity();
        other.updateVelocity();
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
        this.directionX = 0.2;
        this.directionY = -1;

        // Chuẩn hóa hướng
        normalizeDirection();

        updateVelocity();
    }

    public void setPiercing(boolean piercing, long duration) {
        this.isPiercing = piercing;
        if (piercing) {
            this.piercingEndTime = System.currentTimeMillis() + duration;
        } else {
            this.piercingEndTime = 0;
        }
    }

    public void updatePiercingStatus() {
        if (isPiercing && System.currentTimeMillis() >= piercingEndTime) {
            isPiercing = false;
            piercingEndTime = 0;
        }
    }

    public void applySpeedUp(double newSpeed, long durationMillis) {
        this.speed = newSpeed;
        if (directionX != 0 || directionY != 0) {
            normalizeDirection();
            updateVelocity();
        }
        isSpeedUpActive = true;
        speedUpEndTime = System.currentTimeMillis() + durationMillis;
        lastSpeedEffectTime = System.currentTimeMillis();
    }

    public void applySpeedDown(double newSpeed, long durationMillis) {
        this.speed = newSpeed;
        if (directionX != 0 || directionY != 0) {
            normalizeDirection();
            updateVelocity();
        }
        isSpeedDownActive = true;
        speedDownEndTime = System.currentTimeMillis() + durationMillis;
        lastSpeedEffectTime = System.currentTimeMillis();
    }

    private void updateSpeedBoostStatus() {
        long now = System.currentTimeMillis();

        if (isSpeedUpActive && now >= speedUpEndTime) {
            isSpeedUpActive = false;
        }

        if (isSpeedDownActive && now >= speedDownEndTime) {
            isSpeedDownActive = false;
        }

        if (!isSpeedUpActive && !isSpeedDownActive) {
            resetSpeed();
        } else {
            // Nếu cả hai đang chạy → giữ hiệu ứng mới nhất
            if (isSpeedUpActive && isSpeedDownActive) {
                if (speedUpEndTime > speedDownEndTime) {
                    // SpeedUp là mới hơn → giữ nguyên
                } else {
                    // SpeedDown là mới hơn → giữ nguyên
                }
            }
        }
    }
    public void setSpeed(double speed) {
        this.speed = speed;
        normalizeDirection();
        updateVelocity();
    }

    public double getSpeed() {
        return speed;
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

    public boolean isPiercing() {
        return isPiercing;
    }

    public boolean getBallReady() {
        return ballReady;
    }

    public void setBallReady(boolean ballReady) {
        this.ballReady = ballReady;
    }


    public void setDirectionX(double directionX) {
        this.directionX = directionX;
    }

    public void setDirectionY(double directionY) {
        this.directionY = directionY;
    }

    public double getOriginalSpeed() {
        return originalSpeed;
    }

    public Image getBallImage() {
        return ballImage;
    }

    public void setBallImage(Image ballImage) {
        this.ballImage = ballImage;
    }

    public void setPiercing(boolean piercing) {
        isPiercing = piercing;
    }

    public long getPiercingEndTime() {
        return piercingEndTime;
    }

    public void setPiercingEndTime(long piercingEndTime) {
        this.piercingEndTime = piercingEndTime;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Ball(double x, double y, double speed, boolean loadImage) {
        super(0, 0, x, y, 20, 20);
        this.speed = speed;
        this.originalSpeed = speed;
        this.directionX = 0.8;
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();

        normalizeDirection();
        updateVelocity();

        if (loadImage) {
            try {
                ballImage = new Image(getClass().getResource("/com/demo/view/image/balls/58-Breakout-Tiles.png").toExternalForm(), true);
            } catch (Exception e) {
                System.err.println("Không load được ảnh bóng: " + e.getMessage());
            }
        } else {
            ballImage = null;
        }
    }
}
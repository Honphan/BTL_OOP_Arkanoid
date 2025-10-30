package com.demo.model;

import com.demo.util.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lớp Ball đại diện cho quả bóng trong game.
 * Xử lý di chuyển, va chạm, phản xạ, và âm thanh.
 */
public class Ball extends Movable {

    private double speed;
    private double directionX; // hướng bay theo trục X (giá trị từ -1 đến 1)
    private double directionY; // hướng bay theo trục Y (giá trị từ -1 đến 1)
    private int radius;
    private double originalSpeed;
    private boolean isPiercing = false;
    private long piercingEndTime = 0;
    private SoundManager soundManager;

    // --- Constructor ---
    public Ball(double x, double y, double speed) {
        super(0, 0, x, y, 20, 20); // dx, dy sẽ được tính từ hướng bay
        this.radius = 10;
        this.speed = speed;
        this.originalSpeed = speed; // lưu tốc độ gốc
        this.directionX = 0.8;
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();
        updateVelocity(); // tính dx, dy từ hướng bay
    }

    public void resetSpeed() {
        this.speed = originalSpeed;
        updateVelocity();
    }

    /**
     * Chuẩn hóa hướng bay thành vận tốc thực tế (dx, dy) theo tốc độ.
     */
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

    // --- Di chuyển ---
    @Override
    public void move() {
        x += dx;
        y += dy;
    }

    @Override
    public void update() {
        move();
    }

    // --- Vẽ bóng ---
    public void render(GraphicsContext gc) {
        if (isPiercing) {
            gc.setFill(Color.GOLD);
            gc.fillOval(x, y, 2 * radius, 2 * radius);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
            gc.strokeOval(x, y, 2 * radius, 2 * radius);
        } else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, 2 * radius, 2 * radius);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x, y, 2 * radius, 2 * radius);
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
        double ballCenterX = this.x + radius;
        double ballCenterY = this.y + radius;

        double closestX = Math.max(other.getX(), Math.min(ballCenterX, other.getX() + other.getWidth()));
        double closestY = Math.max(other.getY(), Math.min(ballCenterY, other.getY() + other.getHeight()));

        double dx = ballCenterX - closestX;
        double dy = ballCenterY - closestY;

        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        double epsilon = radius * 0.25;

        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");

        if (Math.abs(absDx - absDy) < epsilon * 0.5) {
            bounceHorizontally();
            bounceVertically();
        } else if (absDx > absDy) {
            bounceHorizontally();
            this.x += Math.signum(dx) * 1.0;
        } else {
            bounceVertically();
            this.y += Math.signum(dy) * 1.0;
        }
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

    public void setSpeed(double speed) {
        this.speed = speed;
        updateVelocity();
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

    public boolean isPiercing() {
        return isPiercing;
    }
}
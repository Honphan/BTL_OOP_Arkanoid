package com.demo.model;

import com.demo.util.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends Movable {
    private double speed;
    private double directionX; // -1 = trái, +1 = phải
    private double directionY; // -1 = lên, +1 = xuống
    private int radius;
    private SoundManager soundManager;


    public Ball(double x, double y, double speed) {
        super(0, 0, x, y, 20, 20);
        this.radius = 10;
        this.speed = speed;
        this.directionX = 0.7;
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();
        updateVelocity();
    }

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

    @Override
    public void move() {
        x += dx;
        y += dy;
    }

    @Override
    public void update() {
        move();
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, 2 * radius, 2 * radius);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, 2 * radius, 2 * radius);
    }

    /**
     * Kiểm tra va chạm với GameObject (hình chữ nhật)
     * Dùng thuật toán Circle-Rectangle collision
     */
    public boolean checkCollision(GameObject other) {
        // Tâm bóng
        double ballCenterX = this.x + radius;
        double ballCenterY = this.y + radius;

        // Tìm điểm gần nhất trên hình chữ nhật
        double closestX = ballCenterX;
        if (ballCenterX < other.getX()) {
            closestX = other.getX();
        } else if (ballCenterX > other.getX() + other.getWidth()) {
            closestX = other.getX() + other.getWidth();
        }

        double closestY = ballCenterY;
        if (ballCenterY < other.getY()) {
            closestY = other.getY();
        } else if (ballCenterY > other.getY() + other.getHeight()) {
            closestY = other.getY() + other.getHeight();
        }

        // Tính khoảng cách từ tâm bóng đến điểm gần nhất
        double distanceX = ballCenterX - closestX;
        double distanceY = ballCenterY - closestY;

        // Va chạm nếu khoảng cách <= bán kính
        // Dùng bình phương để tránh sqrt (tối ưu hiệu năng)
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;
        return distanceSquared <= (radius * radius);
    }

    /**
     * Kiểm tra va chạm giữa 2 bóng
     */
    public boolean checkCollisionWithBall(Ball other) {
        double dx = this.getCenterX() - other.getCenterX();
        double dy = this.getCenterY() - other.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (this.radius + other.radius);
    }

    /**
     * Bật bóng theo chiều ngang (trái/phải)
     */
    private void bounceHorizontally() {
        directionX *= -1;
        updateVelocity();
    }

    /**
     * Bật bóng theo chiều dọc (trên/dưới)
     */
    private void bounceVertically() {
        directionY *= -1;
        updateVelocity();
    }

    /**
     * Xử lý bật bóng khi va chạm với hình chữ nhật
     */
    public void bounceOff(GameObject other) {
        double ballCenterX = this.x + radius;
        double ballCenterY = this.y + radius;

        // Tìm điểm gần nhất trên hình chữ nhật
        double closestX = Math.max(other.getX(), Math.min(ballCenterX, other.getX() + other.getWidth()));
        double closestY = Math.max(other.getY(), Math.min(ballCenterY, other.getY() + other.getHeight()));

        // Vector khoảng cách từ tâm bóng tới điểm gần nhất
        double dx = ballCenterX - closestX;
        double dy = ballCenterY - closestY;

        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        double epsilon = radius * 0.25;

        // Âm thanh va chạm
        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");

        if (Math.abs(absDx - absDy) < epsilon * 0.5) {
            // 🔹 Va góc → đảo cả hai hướng
            bounceHorizontally();
            bounceVertically();
        } else if (absDx > absDy) {
            // 🔹 Va cạnh dọc (trái/phải)
            bounceHorizontally();
            if (dx > 0)
                this.x = other.getX() + other.getWidth();
            else
                this.x = other.getX() - 2 * radius;
        } else {
            // 🔹 Va cạnh ngang (trên/dưới)
            bounceVertically();
            if (dy > 0)
                this.y = other.getY() + other.getHeight();
            else
                this.y = other.getY() - 2 * radius;
        }

        // Đẩy nhẹ để tránh kẹt
        this.x += Math.signum(dx) * 0.5;
        this.y += Math.signum(dy) * 0.5;
    }

    /**
     * Xử lý bật bóng khi va chạm với paddle (có góc bật)
     */
    public void bounceOffPaddle(GameObject paddle) {
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
        double ballCenterX = this.getCenterX();
        double hitPosition = (ballCenterX - paddleCenterX) / (paddle.getWidth() / 2);
        hitPosition = Math.max(-1, Math.min(1, hitPosition));

        // Góc bật: -60° (trái) → +60° (phải)
        double angle = hitPosition * Math.toRadians(75);
        directionX = Math.sin(angle);
        directionY = -Math.cos(angle); // luôn bật lên

        updateVelocity();
        this.y = paddle.getY() - 2 * radius;
        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");

    }

    /**
     * Xử lý bật bóng khi va chạm với bóng khác
     */
    /**
     * Xử lý bật bóng khi va chạm với bóng khác
     */
    public void bounceOffBall(Ball other) {
        // 1. Tính vector khoảng cách
        double deltaX = this.getCenterX() - other.getCenterX();
        double deltaY = this.getCenterY() - other.getCenterY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Tránh chia cho 0
        if (distance == 0) {
            distance = 0.01;
            deltaX = Math.random() - 0.5;
            deltaY = Math.random() - 0.5;
        }

        // 2. Đẩy 2 bóng ra nếu bị chồng lấn
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

        // 3. Vector pháp tuyến (normalized)
        double nx = deltaX / distance;
        double ny = deltaY / distance;

        // 4. Chiếu vận tốc lên phương pháp tuyến
        double v1n = this.dx * nx + this.dy * ny;
        double v2n = other.dx * nx + other.dy * ny;

        // 5. Nếu đang tách ra thì không xử lý
        if (v1n - v2n >= 0) return;

        // 6. Hoán đổi vận tốc theo phương pháp tuyến
        double v1nNew = v2n;
        double v2nNew = v1n;

        // 7. Cập nhật dx, dy
        this.dx += (v1nNew - v1n) * nx;
        this.dy += (v1nNew - v1n) * ny;
        other.dx += (v2nNew - v2n) * nx;
        other.dy += (v2nNew - v2n) * ny;

        // 8. Cập nhật direction
        this.directionX = (int) Math.signum(this.dx);
        this.directionY = (int) Math.signum(this.dy);
        other.directionX = (int) Math.signum(other.dx);
        other.directionY = (int) Math.signum(other.dy);
    }

    /**
     * Bật lại khi chạm tường
     */
    public void bounceOffWall(double canvasWidth, double canvasHeight) {
        boolean hitWall = false;
        // Chạm tường trái
        if (this.x <= 0) {
            this.x = 0;
            bounceHorizontally();
            hitWall = true;
        }
        // Chạm tường phải
        else if (this.x + 2 * radius >= canvasWidth) {
            this.x = canvasWidth - 2 * radius;
            bounceHorizontally();
            hitWall = true;
        }

        // Chạm tường trên
        if (this.y <= 0) {
            this.y = 0;
            bounceVertically();
            hitWall = true;
        }
        // Phát âm thanh khi chạm tường
        if (hitWall) {
            soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
        }
    }

    /**
     * Kiểm tra bóng có rơi ra ngoài không
     */
    public boolean isOutOfBounds(double canvasHeight) {
        return this.y > canvasHeight;
    }

    /**
     * Reset vị trí bóng
     */
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
}
package com.demo.model;

import com.demo.Utils.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends Movable {
    private double speed;
    private int directionX; // -1 = trái, +1 = phải
    private int directionY; // -1 = lên, +1 = xuống
    private int radius;
    private SoundManager soundManager;


    public Ball(double x, double y, double speed) {
        super(0, 0, x, y, 20, 20);
        this.radius = 10;
        this.speed = speed;
        this.directionX = 1;   // mặc định đi sang phải
        this.directionY = -1;
        this.soundManager = SoundManager.getInstance();
        updateVelocity();
    }

    private void updateVelocity() {
        this.dx = directionX * speed;
        this.dy = directionY * speed;
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

    // ==================== KIỂM TRA VA CHẠM ====================

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

    // ==================== XỬ LÝ BẬT BÓNG ====================

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

        // Tính khoảng cách theo 2 trục
        double dx = ballCenterX - closestX;
        double dy = ballCenterY - closestY;

        // Ngưỡng để nhận diện va chạm góc
        double epsilon = 0.01;

        if (Math.abs(Math.abs(dx) - Math.abs(dy)) < epsilon) {
            // 🔹 Va chạm góc → đảo cả hai hướng
            bounceHorizontally();
            bounceVertically();

            // Đẩy bóng ra ngoài góc
            if (dx > 0) {
                this.x = other.getX() + other.getWidth();
            } else {
                this.x = other.getX() - 2 * radius;
            }

            if (dy > 0) {
                this.y = other.getY() + other.getHeight();
            } else {
                this.y = other.getY() - 2 * radius;
            }

        } else if (Math.abs(dx) > Math.abs(dy)) {
            // 🔹 Bật ngang
            bounceHorizontally();
            if (dx > 0) {
                this.x = other.getX() + other.getWidth();
            } else {
                this.x = other.getX() - 2 * radius;
            }
        } else {
            // 🔹 Bật dọc
            bounceVertically();
            if (dy > 0) {
                this.y = other.getY() + other.getHeight();
            } else {
                this.y = other.getY() - 2 * radius;
            }
        }
    }
    /**
     * Xử lý bật bóng khi va chạm với paddle (có góc bật)
     */
    public void bounceOffPaddle(GameObject paddle) {
        // Phát âm thanh khi chạm paddle
        soundManager.playSound("/com/demo/view/sound/barkanoid-wallbounce.wav");
        // Tính vị trí bóng chạm vào paddle (-1 đến +1)
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
        double ballCenterX = this.getCenterX();
        double hitPosition = (ballCenterX - paddleCenterX) / (paddle.getWidth() / 2);

        // Giới hạn trong khoảng -1 đến +1
        if (hitPosition < -1) hitPosition = -1;
        if (hitPosition > 1) hitPosition = 1;

        // Điều chỉnh hướng X theo vị trí chạm
        // Chạm giữa: đi thẳng lên, giữ nguyên directionX
        // Chạm rìa trái: đi sang trái
        // Chạm rìa phải: đi sang phải
        if (hitPosition < -0.3) {
            directionX = -1;
        } else if (hitPosition > 0.3) {
            directionX = 1;
        }
        // Nếu từ -0.3 đến 0.3 thì giữ nguyên directionX

        // Luôn bật lên trên
        bounceVertically();

        // Đẩy bóng ra khỏi paddle
        this.y = paddle.getY() - 2 * radius;
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

    // ==================== XỬ LÝ TƯỜNG ====================

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

    // ==================== GETTERS / SETTERS ====================

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        updateVelocity();
    }

    public int getDirectionX() {
        return directionX;
    }

    public int getDirectionY() {
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
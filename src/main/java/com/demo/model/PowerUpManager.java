package com.demo.model;

import com.demo.model.brick.BombBrick;
import com.demo.model.brick.Brick;
import com.demo.model.powerup.PowerUp;
import com.demo.util.SoundManager;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Quản lý danh sách các PowerUp đang tồn tại trong game.
 */
public class PowerUpManager {

    private final List<PowerUp> powerups = new ArrayList<>();
    private double canvasHeight;

    private long piercingEndTime = 0;
    private long laserEndTime = 0;
    private boolean laserMode = false;
    private boolean piercingMode = false;

    private List<Brick> bricks;
    private final List<LaserBullet> laserBullets = new ArrayList<>();

    // Callback khi nhận power-up tức thời (ví dụ: thêm mạng, phát âm thanh)
    private Consumer<PowerUp> onInstantEffect;

    public void setCanvasHeight(double height) {
        this.canvasHeight = height;
    }

    public void setOnInstantEffect(Consumer<PowerUp> callback) {
        this.onInstantEffect = callback;
    }

    public void addPowerUp(PowerUp powerup) {
        if (powerup != null) {
            powerups.add(powerup);
        }
    }

    /**
     * Cập nhật trạng thái các power-up mỗi frame.
     * Loại bỏ nếu đã hết hiệu lực hoặc rơi khỏi màn hình.
     */
    public void update(List<Ball> balls) {
        Iterator<PowerUp> iterator = powerups.iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();
            p.update();

            boolean outOfBounds = p.isFalling() && p.getY() > canvasHeight;
            boolean consumed = p.isConsumed();

            if (outOfBounds || consumed) {
                iterator.remove();
            }
        }
    }


    public void render(GraphicsContext gc) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                p.render(gc);
            }
        }

    }

    /**
     * Kiểm tra va chạm giữa paddle và các power-up.
     * Nếu va chạm xảy ra, xử lý hiệu ứng tương ứng.
     * Power-up tức thời sẽ bị loại ngay.
     * Power-up kéo dài sẽ được giữ lại để tự hủy sau thời gian.
     */
    public void checkCollisionWithPaddle(Paddle paddle, List<Ball> balls, Runnable addLifeCallback) {
        Iterator<PowerUp> iterator = powerups.iterator();
        while (iterator.hasNext()) {
            PowerUp pu = iterator.next();

            if (!pu.isActivated() && checkCollision(pu, paddle)) {
                switch (pu.getType()) {
                    case MULTI_BALL -> {
                        spawnMultiBalls(balls);
                        triggerInstantEffect(pu);
                        iterator.remove(); // loại ngay vì hiệu ứng tức thời
                    }
                    case EXTRA_LIFE -> {
                        if (addLifeCallback != null) addLifeCallback.run();
                        triggerInstantEffect(pu);
                        iterator.remove(); // loại ngay vì hiệu ứng tức thời
                    }

                    case PIERCING_BALL -> {       // ← THÊM CASE NÀY
                    applyPiercingToAllBalls(balls, (int) pu.getDuration());
                    triggerInstantEffect(pu);
                    iterator.remove();
                    }

                    case LASER -> {
                        pu.applyPowerUp(paddle, balls); // paddle.setLaserEnabled(true)
                        pu.activate(paddle);
                        pu.setVisible(false);
                        activateLaser((int) pu.getDuration());
                        triggerInstantEffect(pu);
                    }


                    default -> {
                        pu.applyPowerUp(paddle, balls); // truyền danh sách bóng từ GamePaneController
                        pu.activate(paddle); // để PowerUpManager quản lý thời gian
                        pu.setVisible(false);
                        triggerInstantEffect(pu);
                        // KHÔNG remove ở đây
                    }
                }
            }
        }
    }

    private void spawnMultiBalls(List<Ball> balls) {
        if (balls.isEmpty()) return;

        int currentCount = balls.size();
        int maxBalls = 3;

        if (currentCount >= maxBalls) return; // ❌ Đã đủ bóng, không sinh thêm

        Ball original = balls.get(0);
        double speed = original.getSpeed();
        double angle = Math.atan2(original.getDy(), original.getDx());

        // Tính số bóng cần sinh thêm
        int ballsToAdd = Math.min(maxBalls - currentCount, 2);

        for (int i = 0; i < ballsToAdd; i++) {
            double offsetAngle = Math.toRadians(30 * (i == 0 ? -1 : 1));
            Ball newBall = new Ball(original.getX(), original.getY(), speed);
            newBall.setDx(Math.cos(angle + offsetAngle) * speed);
            newBall.setDy(Math.sin(angle + offsetAngle) * speed);
            newBall.setBallReady(true);
            balls.add(newBall);
        }
    }


    private void triggerInstantEffect(PowerUp pu) {
        if (onInstantEffect != null) {
            onInstantEffect.accept(pu);
        }
    }

    private boolean checkCollision(PowerUp powerUp, Paddle paddle) {
        double puX = powerUp.getX();
        double puY = powerUp.getY();
        double puWidth = powerUp.getWidth();
        double puHeight = powerUp.getHeight();

        double pX = paddle.getX();
        double pY = paddle.getY();
        double pWidth = paddle.getWidth();
        double pHeight = paddle.getHeight();

        return puX < pX + pWidth &&
                puX + puWidth > pX &&
                puY < pY + pHeight &&
                puY + puHeight > pY;
    }

    /**
     * áp dụng piercing ball cho toàn bộ bóng
     */
    private void applyPiercingToAllBalls(List<Ball> balls, int duration) {
        for (Ball ball : balls) {
            ball.setPiercing(true, duration);
        }
        piercingMode = true;
        piercingEndTime = System.currentTimeMillis() + duration;
    }

    /**
     * update trạng thái
     */
    public void updatePiercingMode(List<Ball> balls) {
        if (piercingMode && System.currentTimeMillis() >= piercingEndTime) {
            for (Ball ball : balls) {
                ball.setPiercing(false, 0);
            }
            piercingMode = false;
        }

        for (Ball ball : balls) {
            ball.updatePiercingStatus();
        }
    }

    private void activateLaser(int durationMs) {
        laserMode = true;
        laserEndTime = System.currentTimeMillis() + durationMs;
    }

    public void updateLaserMode(Paddle paddle) {
        if (laserMode && System.currentTimeMillis() >= laserEndTime) {
            paddle.setLaserEnabled(false);
            laserMode = false;
        }

        if (paddle.isLaserEnabled() && paddle.canShootLaser()) {
            paddle.markLaserShot(); // ✅ cập nhật thời gian bắn sau khi tạo đạn
            spawnLaserBullets(paddle);
        }
        updateLaserBullets();
    }

    private void spawnLaserBullets(Paddle paddle) {
        double laserOffset = 10;
        double leftX = paddle.getX() + laserOffset;
        double rightX = paddle.getX() + paddle.getWidth() - laserOffset;
        double y = paddle.getY() - 20;

        laserBullets.add(new LaserBullet(leftX, y));
        laserBullets.add(new LaserBullet(rightX, y));

        SoundManager.getInstance().playSound("/com/demo/view/sound/laser-312360 (mp3cut.net) (1).mp3");
    }

    private void updateLaserBullets() {
        Iterator<LaserBullet> iterator = laserBullets.iterator();
        while (iterator.hasNext()) {
            LaserBullet bullet = iterator.next();
            bullet.update();

            for (Brick brick : bricks) {
                if (brick == null || brick.isDestroyed()) continue; // ✅ bỏ qua gạch đã phá

                if (bullet.checkCollisionArea(brick)) {
                    if (brick.isDestructible()) {

                        if (brick instanceof BombBrick) {
                            brick.takeHit();
                        }
                        else {
                            brick.takeHit();

                            SoundManager.getInstance().playSound("/com/demo/view/sound/barkanoid-laser.wav");
                        }
                    }
                    bullet.destroy();
                    break;
                }
            }

            if (!bullet.isActive()) {
                iterator.remove();
            }
        }
    }

    public void renderLaserBullets(GraphicsContext gc) {
        for (LaserBullet bullet : laserBullets) {
            bullet.render(gc);
        }
    }

    private boolean canShootLaser(Paddle paddle) {
        long now = System.currentTimeMillis();
        long lastShot = paddle.getLastLaserShotTime();
        return now - lastShot >= 500; // LASER_COOLDOWN
    }



    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }

    public List<PowerUp> getPowerUps() {
        return powerups;
    }
}

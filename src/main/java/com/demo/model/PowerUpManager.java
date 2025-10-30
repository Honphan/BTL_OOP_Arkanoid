package com.demo.model;

import com.demo.model.powerup.PowerUp;
import com.demo.model.powerup.PowerUpType;
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
            p.update(); // PowerUp tự kiểm tra thời gian và gọi deactivate()

            boolean expired = !p.isFalling() && !p.isActivated() && p.getDuration() > 0;
            boolean outOfBounds = p.isFalling() && p.getY() > canvasHeight;

            if (expired || outOfBounds) {
                if (expired) {
                    p.removePowerUp(p.getTarget(), balls); // ✅ khôi phục trạng thái bóng hoặc paddle
                }
                iterator.remove();
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (PowerUp p : powerups) {
            p.render(gc);
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
                    default -> {
                        pu.applyPowerUp(paddle, balls); // truyền danh sách bóng từ GamePaneController
                        pu.activate(paddle); // để PowerUpManager quản lý thời gian
                        triggerInstantEffect(pu);
                        // KHÔNG remove ở đây
                    }
                }
            }
        }
    }

    private void spawnMultiBalls(List<Ball> balls) {
        if (balls.isEmpty()) return;

        Ball original = balls.get(0);
        double speed = original.getSpeed();
        double angle = Math.atan2(original.getDy(), original.getDx());

        Ball ball1 = new Ball(original.getX(), original.getY(), speed);
        Ball ball2 = new Ball(original.getX(), original.getY(), speed);

        double angle1 = angle - Math.toRadians(30);
        double angle2 = angle + Math.toRadians(30);

        ball1.setDx(Math.cos(angle1) * speed);
        ball1.setDy(Math.sin(angle1) * speed);
        ball2.setDx(Math.cos(angle2) * speed);
        ball2.setDy(Math.sin(angle2) * speed);

        balls.add(ball1);
        balls.add(ball2);
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

    public List<PowerUp> getPowerUps() {
        return powerups;
    }
}

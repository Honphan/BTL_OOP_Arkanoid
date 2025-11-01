package com.demo.model.powerup;

import com.demo.model.Ball;
import com.demo.model.GameObject;
import com.demo.model.Movable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Lớp trừu tượng cho các PowerUp — vật thể rơi có hiệu ứng trong thời gian giới hạn.
 * Mỗi PowerUp có thể rơi xuống, được kích hoạt khi va chạm với paddle,
 * và tự động hết hiệu lực sau một khoảng thời gian.
 */
public abstract class PowerUp extends GameObject {

    private static final long DEFAULT_DURATION = 10000L;
    protected final PowerUpType type;
    protected final long duration;
    protected final double velocityY;
    protected final Image image;
    private Movable target;

    // Trạng thái: đang rơi hay đã được kích hoạt
    private boolean falling = true;
    private boolean activated = false;
    private boolean consumed = false;
    private boolean visible = true;

    private long activationTime = 0;

    /**
     * Tạo power-up với thời gian hiệu lực tùy chỉnh.
     */
    public PowerUp(double x, double y, double width, double height,
                   PowerUpType type, long duration, double velocityY, Image image) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
        this.velocityY = velocityY;
        this.image = image;
    }

    /**
     * Tạo power-up với thời gian hiệu lực mặc định.
     */
    public PowerUp(double x, double y, double width, double height,
                   PowerUpType type, double velocityY, Image image) {
        this(x, y, width, height, type, DEFAULT_DURATION, velocityY, image);
    }

    // --- Update logic ---

    /**
     * Cập nhật trạng thái mỗi frame:
     * - Nếu đang rơi: di chuyển xuống.
     * - Nếu đã kích hoạt: kiểm tra hết thời gian để tự hủy.
     */
    @Override
    public void update() {
        if (falling) {
            y += velocityY;
        } else if (activated) {
            long now = System.currentTimeMillis();
            if (now - activationTime >= duration) {
                deactivate();
                setConsumed(true);
            }
        }
    }

    /**
     * Kiểm tra xem power-up đã hết thời gian hiệu lực chưa.
     */

    // --- Rendering ---

    /**
     * Vẽ power-up lên canvas với hiệu ứng ánh sáng khi đang rơi.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (image == null) return;

        gc.save();
        if (falling) {
            gc.setEffect(new DropShadow(15, Color.web("#00FFFF", 0.8)));
            gc.setGlobalAlpha(0.9);
        }
        gc.drawImage(image, x, y, width, height);
        gc.restore();
    }

    // --- Activation & Deactivation ---

    /**
     * Kích hoạt power-up khi va chạm với paddle.
     * Gọi phương thức applyPowerUp() và bắt đầu đếm thời gian.
     */

    public void activate(Movable target) {
        if (!activated && target != null) {
            this.target = target;
            this.falling = false;
            this.activated = true;
            this.activationTime = System.currentTimeMillis(); // ✅ lưu thời điểm kích hoạt
            applyPowerUp(target);
        }
    }

    /**
     * Hủy hiệu ứng power-up khi hết thời gian.
     * Gọi phương thức removePowerUp().
     */
    public void deactivate() {
        System.out.println("Deactivating PowerUp: " + type);
        if (activated && target != null) {
            removePowerUp(target);
        }
        activated = false;
        target = null;
    }

    // --- Abstract methods ---

    /**
     * Cài đặt hiệu ứng khi power-up được kích hoạt.
     */
    public abstract void applyPowerUp(Movable target);

    public void applyPowerUp(Movable target, List<Ball> balls) {
        applyPowerUp(target); // mặc định gọi bản cũ nếu không cần bóng
    }

    /**
     * Gỡ bỏ hiệu ứng khi power-up hết thời gian.
     */
    public abstract void removePowerUp(Movable target);

    public void removePowerUp(Movable target, List<Ball> balls) {
        removePowerUp(target); // mặc định nếu không cần bóng
    }

    // --- Getters ---

    public PowerUpType getType() { return type; }

    public long getDuration() { return duration; }

    public boolean isFalling() { return falling; }

    public boolean isActivated() { return activated; }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Movable getTarget() { return target; }

    public boolean isConsumed() { return consumed; }

    public void setConsumed(boolean consumed) { this.consumed = consumed; }

    public static long getDefaultDuration() { return DEFAULT_DURATION; }
}

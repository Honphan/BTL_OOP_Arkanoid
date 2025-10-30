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

    // Thời gian hiệu lực mặc định (ms)
    private static final long DEFAULT_DURATION = 8000L;

    // Loại power-up (ví dụ: EXPAND_PADDLE, EXTRA_LIFE, MULTI_BALL...)
    protected final PowerUpType type;

    // Thời gian hiệu lực (ms)
    protected final long duration;

    // Tốc độ rơi theo trục Y
    protected final double velocityY;

    // Hình ảnh hiển thị power-up
    protected final Image image;

    // Đối tượng đang nhận hiệu ứng (thường là Paddle)
    private Movable target;

    // Trạng thái: đang rơi hay đã được kích hoạt
    private boolean falling = true;
    private boolean activated = false;

    // Thời điểm bắt đầu hiệu lực (ms)
    private long startTime = 0;

    // --- Constructor ---

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
        } else {
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("⏱ PowerUp " + type + " active for " + elapsed + "ms");
            if (isExpired()) {
                System.out.println("⛔ PowerUp " + type + " expired");
                deactivate();
            }
        }
    }
    /**
     * Kiểm tra xem power-up đã hết thời gian hiệu lực chưa.
     */
    private boolean isExpired() {
        return activated && duration > 0 &&
                System.currentTimeMillis() - startTime >= duration;
    }

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
            this.startTime = System.currentTimeMillis();
            applyPowerUp(target);
        }
    }

    /**
     * Hủy hiệu ứng power-up khi hết thời gian.
     * Gọi phương thức removePowerUp().
     */
    public void deactivate() {
        System.out.println("🔻 Deactivating PowerUp: " + type);
        if (activated && target != null) {
            removePowerUp(target);
        }
        activated = false;
        target = null;
        startTime = 0;
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

    public long getStartTime() { return startTime; }

    public Movable getTarget() { return target; }

    public static long getDefaultDuration() { return DEFAULT_DURATION; }
}

package com.demo.model.powerup;

import com.demo.model.GameObject;
import com.demo.model.Movable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public abstract class PowerUp extends GameObject {

    protected PowerUpType type;
    protected int duration;   // thời gian hiệu ứng tồn tại (ms hoặc tick)
    protected double velocityY;
    protected Image image;
    protected boolean active; // power-up còn đang rơi hay đã kích hoạt

    public PowerUp(double x, double y, double width, double height, PowerUpType type, int duration, double velocityY, Image image) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
        this.velocityY = velocityY;
        this.image = image;
        this.active = true;
    }

    /** Cập nhật vị trí và trạng thái power-up mỗi frame */
    @Override
    public void update() {
        if (!active) return;
        y += velocityY;
    }

    /** Vẽ power-up trên canvas */
    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;
        if (image != null) {
            gc.save();

            // Làm cho power-up phát sáng (hiệu ứng viền sáng)
            DropShadow glow = new DropShadow();
            glow.setRadius(15);
            glow.setColor(Color.web("#00FFFF", 0.8)); // xanh cyan nhẹ

            gc.setEffect(glow);
            gc.setGlobalAlpha(0.9);

            gc.drawImage(image, x, y, width, height);

            gc.restore();
        }
    }

    /** Khi paddle hứng được power-up */
    public abstract void applyPowerUp(Movable target);

    /** Khi hiệu ứng power-up kết thúc */
    public abstract void removePowerUp(Movable target);

    public PowerUpType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

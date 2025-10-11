package com.demo.model.bricks;

import com.demo.model.Ball;
import com.demo.model.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Brick extends GameObject {
    private int hitPoints;  // Điểm máu của khối gạch
    private String type;  // Loại gạch
    private boolean destroyed;  // Kiểm tra gạch bị phá chưa

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
        this.destroyed = false;
    }

    // Kiểm tra va chạm với bóng
    public boolean checkCollision(Ball ball) {
        double ballX = ball.getX();
        double ballY = ball.getY();
        double radius = ball.getRadius();

        boolean collision = ballX + radius >= x && ballX - radius <= x + width
                && ballY + radius >= y && ballY - radius <= y + height;

        if (collision) {
            takeHit(); // giảm máu hoặc phá hủy gạch
        }

        return collision;
    }

    public abstract void update();

    public abstract void render(GraphicsContext gc);

    public abstract void takeHit();

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public boolean isDestroyed() { return destroyed; }

    public int getHitPoints() { return hitPoints; }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }
}

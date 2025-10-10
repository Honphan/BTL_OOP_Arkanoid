package com.demo.model.brick;

import com.demo.model.ball.Ball;
import com.demo.model.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

public abstract class  Brick extends GameObject {
    private int hitPoints;  // Điểm máu của khối gạch
    private String type;  // Loại gạch
    private boolean destroyed;  // Kiểm tra gạch bị phá chưa

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
        this.destroyed = false;
    }

    public abstract void update();

    public abstract void render(GraphicsContext gc);

    // Trừ điểm máu khi va chạm
    public abstract void takeHit();

    // Kiểm tra va chạm với bóng
    public boolean checkCollision(Ball ball) {
        double ballX = ball.getX();
        double ballY = ball.getY();
        double radius = ball.getRadius();

        boolean collision = ballX + radius >= x && ballX - radius <= x + width
                && ballY + radius >= y && ballY - radius <= y + height;

        if (collision) {
            takeHit();
        }
        return collision;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }
    public boolean getDestroyed() { return destroyed; }

    public int getHitPoints() { return hitPoints; }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }
}

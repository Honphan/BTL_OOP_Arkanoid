package com.demo.model.brick;

import com.demo.model.Ball;
import com.demo.model.GameObject;
import javafx.scene.canvas.GraphicsContext;

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
        if (destroyed) return false;

        // Kiểm tra va chạm bằng thuật toán có sẵn trong Ball
        if (ball.checkCollision(this)) {
            // Bóng phản xạ lại
            ball.bounceOff(this);
            takeHit();
            return true;
        }
        return false;
    }

    public boolean isDestructible() {
        return true; // mặc định là có thể phá
    }

    public boolean shouldBounceBall() {
        return true; // mặc định là bóng nên phản xạ
    }

    public int getScoreValue(){
        return 10;
    }

//    public abstract int getScoreValue();

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

package com.demo.model;

import com.demo.model.brick.Brick;
import com.sun.source.tree.TryTree;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;


public class LaserBullet {
    private double x, y;
    private final double width = 5;
    private final double height = 15;
    private final double speed = 8;
    private boolean active = true;

    private Image image;

    public LaserBullet(double x, double y) {
        this.x = x;
        this.y = y;

        try {
            image = new Image(getClass().getResourceAsStream("/com/demo/view/image/powerups/bangbang.png"));
        } catch (Exception e) {
            System.err.println("Lỗi không load được ảnh đạn: " + e.getMessage());
        }
    }

    public void update() {
        y -= speed;
        if (y + height < 0) active = false;
    }

    public void render(GraphicsContext gc) {
        if (!active) return;
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean checkCollisionArea(Brick brick) {
        return active &&
                x < brick.getX() + brick.getWidth() &&
                x + width > brick.getX() &&
                y < brick.getY() + brick.getHeight() &&
                y + height > brick.getY();
    }


    public void destroy() {
        active = false;
    }
}

package com.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends Movable {
    private double speed;

    public Paddle(double x, double y, double width, double height, double speed, double dx, double dy) {
        super(dx, dy, 0, 0, 0, 0);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
    
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, width, height);
    }
    
    // Thêm phương thức update để cập nhật vị trí dựa trên dx
    public void update(double canvasWidth) {
        // Cập nhật vị trí dựa trên hướng di chuyển
        x += dx;
        
        // Giới hạn không cho paddle vượt ra ngoài màn hình
        if (x < 0) {
            x = 0;
        } else if (x + width > canvasWidth) {
            x = canvasWidth - width;
        }
    }
    
    // Các phương thức di chuyển không cần tham số canvasWidth nữa
    public void moveLeft() {
        // Đặt hướng di chuyển sang trái
        dx = -speed;
    }
    
    public void moveRight() {
        // Đặt hướng di chuyển sang phải
        dx = speed;
    }
    
    public void stop() {
        // Dừng di chuyển
        dx = 0;
    }
    
    // Getters và setters
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    @Override
    public void update() {

    }

    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void move() {

    }
}
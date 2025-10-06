package com.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle {
    private double x;
    private double y;
    private double width;
    private double height;
    private double speed;
    private double canvasWidth; // Đã sửa tên biến để tuân theo quy ước đặt tên
    private double dx = 0; // Thêm biến để lưu hướng di chuyển
    
    public Paddle(double x, double y, double width, double height, double speed, double canvasWidth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.canvasWidth = canvasWidth;
    }
    
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, width, height);
    }
    
    // Thêm phương thức update để cập nhật vị trí dựa trên dx
    public void update() {
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
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setCanvasWidth(double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }
}
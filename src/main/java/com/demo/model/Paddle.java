package com.demo.model;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Paddle extends Movable {
    private double speed;
    private PowerUp currentPowerUp; // null nếu không có power-up
    private double canvasWidth; // Để giới hạn di chuyển


    public Paddle(double x, double y, double width, double height, double speed, double canvasWidth) {
        super(0, 0, x, y, width, height);
        this.speed = speed;
        this.currentPowerUp = null;
        this.canvasWidth = canvasWidth;
    }


    @Override
    public void move() {
        x += dx;


        // Giới hạn không cho paddle vượt ra ngoài màn hình
        if (x < 0) {
            x = 0;
        } else if (x + width > canvasWidth) {
            x = canvasWidth - width;
        }
    }


    @Override
    public void update() {
        move();
    }


    public void render(GraphicsContext gc) {
        if (currentPowerUp != null) {
            gc.setFill(Color.GOLD); // Màu vàng khi có power-up
        } else {
            gc.setFill(Color.BLUE);
        }


        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }
    /**
     * Di chuyển sang trái
     */
    public void moveLeft() {
        this.dx = -speed;
    }
    /**
     * Di chuyển sang phải
     */
    public void moveRight() {
        this.dx = speed;
    }
    /**
     * Dừng di chuyển
     */
    public void stop() {
        this.dx = 0;
    }



    public double getSpeed() {
        return speed;
    }


    public void setSpeed(double speed) {
        this.speed = speed;
    }


    public PowerUp getCurrentPowerUp() {
        return currentPowerUp;
    }


    public void setCanvasWidth(double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }


    public double getCenterX() {
        return x + width / 2;
    }


    public double getCenterY() {
        return y + height / 2;
    }
}


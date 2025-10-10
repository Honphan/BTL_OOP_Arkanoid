package com.demo.model;

import com.demo.Utils.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Paddle extends Movable {
    private double speed;
    private PowerUp currentPowerUp;
    private Image paddleImage;
    private Image paddlePowerUpImage; // Hình ảnh khi có power-up

    public Paddle(double x, double y, double width, double height, double speed) {
        super(0, 0, x, y, width, height);
        this.speed = speed;
        this.currentPowerUp = null;
        loadImages();
    }

    private void loadImages() {
        try {
            // Load hình ảnh paddle gốc
            Image originalImage = new Image(getClass().getResource("/com/demo/view/image/paddle/paddle_laser2.png").toExternalForm());

            // Xử lý để loại bỏ background
            paddleImage = Util.removeBackground(originalImage);
            System.out.println("Tải và xử lý paddle_laser2.png thành công!");

        } catch (Exception e) {
            System.err.println("Lỗi khi load hình ảnh paddle: " + e.getMessage());
            e.printStackTrace();
            paddleImage = null;
            paddlePowerUpImage = null;
        }
    }


    public void update(double canvasWidth) {
        x += dx;
        if (x < 0) {
            x = 0;
        } else if (x + width > canvasWidth) {
            x = canvasWidth - width;
        }
    }

    @Override
    public void update() {
        // Implementation rỗng
    }

    public void render(GraphicsContext gc) {
        if (paddleImage != null) {
            // Sử dụng hình ảnh thay vì vẽ hình chữ nhật
            Image imageToUse = (currentPowerUp != null && paddlePowerUpImage != null)
                    ? paddlePowerUpImage
                    : paddleImage;

            gc.drawImage(imageToUse, x, y, width, height);
        } else {
            // Fallback: vẽ hình chữ nhật nếu không load được hình ảnh
            if (currentPowerUp != null) {
                gc.setFill(Color.GOLD);
            } else {
                gc.setFill(Color.BLUE);
            }
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, width, height);
        }
    }

    public void moveLeft() {
        this.dx = -speed;
    }

    public void moveRight() {
        this.dx = speed;
    }

    public void stop() {
        this.dx = 0;
    }

    // Getters và setters giữ nguyên
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public PowerUp getCurrentPowerUp() {
        return currentPowerUp;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }

    @Override
    public void move() {
        // Implementation rỗng
    }
}

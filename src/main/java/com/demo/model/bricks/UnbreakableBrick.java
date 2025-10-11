package com.demo.model.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class UnbreakableBrick extends Brick {

    private Image brickImage;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, "unbreakable"); // Máu vô hạn

        try {
            brickImage = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/unbreakable.png"));
        } catch (Exception e) {
            System.out.println("Không thể load ảnh cho UnbreakableBrick: " + e.getMessage());
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return; // nếu có flag destroyed (hiếm khi)

        if (brickImage != null) {
            gc.drawImage(brickImage, getX(), getY(), getWidth(), getHeight());
        } else {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void takeHit() {
    }
}

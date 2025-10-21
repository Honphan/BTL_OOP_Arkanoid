package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class UnbreakableBrick extends Brick {

    private Image image;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, "unbreakable"); // HP vô hạn

        try {
            image = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/unbreakable.png"));
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch: " + e.getMessage());
        }
    }

    @Override
    public void takeHit() {
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }
}

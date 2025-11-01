package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Gạch không phá được, có hiệu ứng ánh sáng chéo khi va chạm.
 */
public class UnbreakableBrick extends Brick {

    private Image image;

    // Hiệu ứng ánh sáng chéo
    private boolean flashing = false;
    private double flashProgress = 0;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, "unbreakable");

        try {
            image = new Image(getClass().getResource("/com/demo/view/image/bricks/unbreakable.png").toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch không phá được: " + e.getMessage());
        }
    }

    @Override
    public boolean isDestructible() {
        return false;
    }

    @Override
    public boolean shouldBounceBall() {
        return true;
    }

    @Override
    public void takeHit() {
        flashing = true;
        flashProgress = 0;
    }

    @Override
    public void update() {
        if (flashing) {
            flashProgress += 0.08;
            if (flashProgress >= 1.2) {
                flashProgress = 0;
                flashing = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ ảnh gạch
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        }

        // Vẽ hiệu ứng ánh sáng chéo gradient
        if (flashing) {
            double beamWidth = width * 0.3;
            double centerOffset = width * flashProgress;

            // Vẽ nhiều lớp tia sáng xiên để tạo gradient
            for (int i = -6; i <= 6; i++) {
                double alpha = 0.08 * (1.0 - Math.abs(i) / 6.0); // mờ dần ra hai bên
                gc.setGlobalAlpha(alpha);

                double offset = centerOffset + i * 2;
                double x1 = x + offset - beamWidth / 2;
                double x2 = x + offset + beamWidth / 2;
                double y1 = y;
                double y2 = y + height;

                // Giới hạn trong viên gạch
                x1 = Math.max(x, Math.min(x + width, x1));
                x2 = Math.max(x, Math.min(x + width, x2));

                double[] polyX = {x1, x2, x2, x1};
                double[] polyY = {y1, y1, y2, y2};

                gc.setFill(Color.WHITE);
                gc.fillPolygon(polyX, polyY, 4);
            }

            gc.setGlobalAlpha(1.0);
        }
    }
}

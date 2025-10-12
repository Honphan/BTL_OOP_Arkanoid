package com.demo.model.bricks;

import com.demo.model.Ball;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

// Gạch bình thường: giảm máu khi trúng bóng, có thể bị phá hủy
public class NormalBrick extends Brick {

    private Image[] imagebricks;

    public NormalBrick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height, hitPoints, "normal");

        imagebricks = new Image[6];

        try {

            imagebricks[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/1hp_green1.png"));
            imagebricks[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/2hp_violet1.png"));
            imagebricks[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/3hp_blue1.png"));
            imagebricks[4] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/4hp_yellow1.png"));
            imagebricks[5] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/5hp_red1.png"));

        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch: " + e.getMessage());
        }

    }

    @Override
    public void takeHit() {
        if (!isDestroyed()) {
            setHitPoints(getHitPoints() - 1);
            if (getHitPoints() <= 0) {
                setDestroyed(true);
            }
        }
    }

    @Override
    public void update() {
        // NormalBrick không có hành vi đặc biệt, chỉ cần check destroyed trong game loop
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return;

        Image img = switch (getHitPoints()) {
            case 5 -> imagebricks[5];
            case 4 -> imagebricks[4];
            case 3 -> imagebricks[3];
            case 2 -> imagebricks[2];
            default -> imagebricks[1];
        };

        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        }


    }
}
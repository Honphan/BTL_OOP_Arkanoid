package com.demo.model.brick;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class HiddenBrick extends Brick{

    private Image image;
    private boolean revealed;
    private double opacity = 0;
    private static final int MAX_HP = 2;

    public HiddenBrick(double x, double y, double brickWidth, double brickHeight) {
        super(x, y, brickWidth, brickHeight, 2, "hidden");
        revealed = false;

        try {
            image = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/red-normal.png"));
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch ẩn: " + e.getMessage());;
        }
    }

    @Override
    public void takeHit() {
        if (isDestroyed()) return;

        setHitPoints(getHitPoints() - 1);

        // Khi bị đánh lần đầu → hiện ra
        if (getHitPoints() == 1) {
            revealed = true;
        }

        // Khi máu còn 0 → phá hủy
        if (getHitPoints() <= 0) {
            setDestroyed(true);
        }
    }

    @Override
    public void update() {
        if (revealed && opacity < 1) {
            opacity += 0.05;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return;
        if (!revealed) return;

        gc.setGlobalAlpha(opacity);
        gc.drawImage(image, x, y, width, height);
        gc.setGlobalAlpha(1.0);
    }


    public int getScoreValue() {
        return 30; // Điểm cao hơn vì khó hơn
    }



}

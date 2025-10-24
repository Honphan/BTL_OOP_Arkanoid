package com.demo.model.brick;

import com.demo.model.Ball;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BombBrick extends Brick {
    private final Brick[][] field;
    private final int row, col;

    private static final int MAX_HP = 1;

    // Ảnh viên bomb
    private Image image;

    // Trạng thái nổ
    private boolean exploding = false;
    private double explosionRadius = 0;
    private double explosionOpacity = 1.0;

    // Thông số hiệu ứng
    private static final double MAX_RADIUS_MULTIPLIER = 3.0;
    private static final double EXPLOSION_GROW_SPEED = 8;
    private static final double EXPLOSION_FADE_SPEED = 0.05;

    public BombBrick(double x, double y, double width, double height, Brick[][] field, int row, int col) {
        super(x, y, width, height, MAX_HP, "bomb");
        this.field = field;
        this.row = row;
        this.col = col;

        // Tải ảnh bomb
        try {
            image = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/invincible_white1.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không load được ảnh gạch: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        if (exploding) {
            explosionRadius += EXPLOSION_GROW_SPEED;
            explosionOpacity -= EXPLOSION_FADE_SPEED;
            if (explosionOpacity <= 0) {
                exploding = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        double w = getWidth();
        double h = getHeight();

        // Nếu đang nổ → vẽ hiệu ứng nổ
        if (exploding) {
            double centerX = x + w / 2;
            double centerY = y + h / 2;
            double base = Math.max(w, h);
            double radius = base * (1 + (explosionRadius / (base * MAX_RADIUS_MULTIPLIER)));

            gc.setGlobalAlpha(Math.max(0, explosionOpacity));

            // Vẽ 3 lớp vòng sáng màu
            gc.setFill(Color.rgb(255, 255, 150, 0.9)); // lõi sáng vàng
            gc.fillOval(centerX - radius * 0.4, centerY - radius * 0.4, radius * 0.8, radius * 0.8);

            gc.setFill(Color.rgb(255, 150, 50, 0.6)); // cam nhạt
            gc.fillOval(centerX - radius * 0.7, centerY - radius * 0.7, radius * 1.4, radius * 1.4);

            gc.setFill(Color.rgb(255, 50, 0, 0.35)); // đỏ nhạt ngoài cùng
            gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            gc.setGlobalAlpha(1.0);
            return;
        }

        // Nếu chưa bị phá hủy → vẽ ảnh bomb
        if (!isDestroyed() && image != null) {
            gc.drawImage(image, x, y, w, h);
        }
    }

    @Override
    public void takeHit() {
        if (isDestroyed()) return;

        setHitPoints(0);
        setDestroyed(true);
        startExplosion();
        explode();
    }

    private void startExplosion() {
        exploding = true;
        explosionRadius = 0;
        explosionOpacity = 1.0;
    }

    /**
     * Phá hủy 3 viên gạch theo 4 hướng: trên, dưới, trái, phải.
     */
    private void explode() {
        int radius = 3;

        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                int r = row + dr;
                int c = col + dc;

                // Kiểm tra biên
                if (r < 0 || c < 0 || r >= field.length || c >= field[r].length)
                    continue;

                Brick neighbor = field[r][c];
                if (neighbor == null || neighbor.isDestroyed())
                    continue;

                if (neighbor instanceof BombBrick) {
                    BombBrick bb = (BombBrick) neighbor;
                    if (!bb.isDestroyed()) {
                        bb.setDestroyed(true);
                        bb.startExplosion();
                        Platform.runLater(() ->bb.explode()); // lan nổ dây chuyền
                    }
                } else {
                    neighbor.setDestroyed(true);
                }
            }
        }
    }

    public boolean isExploding() {
        return exploding;
    }


    @Override
    public boolean checkCollision(Ball ball) {
        return super.checkCollision(ball);
    }

    @Override
    public int getScoreValue() {
        return 50;
    }
}
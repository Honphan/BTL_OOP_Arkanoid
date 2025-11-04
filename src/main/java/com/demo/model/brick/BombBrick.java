package com.demo.model.brick;

import com.demo.model.Ball;
import com.demo.util.SoundManager;
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
            image = new Image(getClass().getResource("/com/demo/view/image/bricks/bombbrick.png").toExternalForm(), true);
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch nổ: " + e.getMessage());
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

        if (exploding) {
            double centerX = x + w / 2;
            double centerY = y + h / 2;
            double base = Math.max(w, h);
            double radius = base * (1 + (explosionRadius / (base * MAX_RADIUS_MULTIPLIER)));

            centerX += Math.random() * 4 - 2;
            centerY += Math.random() * 4 - 2;

            gc.setGlobalAlpha(Math.max(0, explosionOpacity));

            // Vòng sáng cam đậm
            gc.setFill(Color.rgb(255, 120, 0, 0.5));
            gc.fillOval(centerX - radius * 0.6, centerY - radius * 0.6, radius * 1.2, radius * 1.2);

            // Vòng sáng đỏ nhạt ngoài cùng
            gc.setFill(Color.rgb(255, 40, 0, 0.3));
            gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // Khói mờ xám
            gc.setFill(Color.rgb(80, 80, 80, 0.15));
            gc.fillOval(centerX - radius * 1.2, centerY - radius * 1.2, radius * 2.4, radius * 2.4);

            gc.setGlobalAlpha(1.0);
            return;
        }

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

        SoundManager.getInstance().playSound("/com/demo/view/sound/mixkit-far-away-war-explosions-2786 (mp3cut.net) (1).wav");
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
                        Platform.runLater(() -> bb.explode());
                    }
                } else {
                    if (neighbor.isDestructible()) {
                        if (neighbor instanceof PowerUpBrick puBrick) {
                            puBrick.setDisableSpawn(true); // không cho sinh PowerUp
                        }
                        neighbor.takeHit(); // dùng takeHit để xử lý đúng logic
                    }
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

    public Brick[][] getField() {
        return field;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setExploding(boolean exploding) {
        this.exploding = exploding;
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    public double getExplosionOpacity() {
        return explosionOpacity;
    }

    public void setExplosionOpacity(double explosionOpacity) {
        this.explosionOpacity = explosionOpacity;
    }
}
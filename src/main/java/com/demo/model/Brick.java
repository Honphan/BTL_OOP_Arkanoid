package com.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GameObject {
    private int hitPoints;  // Điểm máu của khối gạch
    private String type;  // Loại gạch
    private boolean destroyed;  // Kiểm tra gạch bị phá chưa

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
        this.destroyed = false;
    }

    @Override
    public void update() {
        // Gạch không di chuyển
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!destroyed) {
            // Chọn màu theo độ bền hoặc loại
            Color color = switch (type) {
                case "unbreakable" -> Color.GREY;
                default -> switch (hitPoints) {
                    case 3 -> Color.RED;
                    case 2 -> Color.GREEN;
                    case 1 -> Color.ORANGE;
                    default -> Color.BLACK;
                };
            };

            // Vẽ khối gạch hình chữ nhật
            gc.setFill(color);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    // Giảm máu gạch khi bóng va chạm
    public void takeHit() {
        if (!destroyed && !"unbreakable".equals(type)) {
            hitPoints--;
            if (hitPoints <= 0) destroyed = true;
        }
    }

    public boolean isDestroyed() { return destroyed; }

    public int getHitPoints() { return hitPoints; }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }
}

package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

public class NormalBrick extends Brick {

    private boolean cracked;
    private boolean heavilyCracked;

    public NormalBrick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height, hitPoints, "normal");

        this.cracked = false;
        this.heavilyCracked = false;
    }

    @Override
    public void update() {
        // Không di chuyển, không cần cập nhật
    }

    @Override
    public void render(GraphicsContext gc) {
        if (getDestroyed()) return;

        drawBrickBody(gc);
        drawCrack(gc);
        drawEdgeLight(gc);
    }

    @Override
    public void takeHit() {
        if (getDestroyed()) return;

        int newHP = getHitPoints() - 1;
        setHitPoints(newHP);

        if (newHP == 2) cracked = true;
        else if (newHP == 1) heavilyCracked = true;
        else if (newHP <= 0) setDestroyed(true);
    }

    // =================== HÀM VẼ HIỆU ỨNG ===================

    /** Vẽ phần thân gạch với gradient sáng trên - tối dưới */
    private void drawBrickBody(GraphicsContext gc) {
        Color baseColor = switch (getHitPoints()) {
            case 3 -> Color.web("#2ecc71"); // xanh
            case 2 -> Color.web("#e67e22"); // cam
            default -> Color.web("#e74c3c"); // đỏ
        };

        var gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, baseColor.brighter()),
                new Stop(0.5, baseColor),
                new Stop(1.0, baseColor.darker())
        );

        gc.setFill(gradient);
        gc.fillRect(x, y, width, height);

        // Viền
        gc.setStroke(Color.rgb(0, 0, 0, 0.4));
        gc.strokeRect(x, y, width, height);
    }

    /** Vẽ viền sáng phía trên và tối phía dưới cho hiệu ứng “nổi khối” */
    private void drawEdgeLight(GraphicsContext gc) {
        // Viền sáng trên
        gc.setStroke(Color.rgb(255, 255, 255, 0.6));
        gc.strokeLine(x, y, x + width, y); // cạnh trên
        gc.strokeLine(x, y, x, y + height); // cạnh trái

        // Viền tối dưới
        gc.setStroke(Color.rgb(0, 0, 0, 0.5));
        gc.strokeLine(x, y + height, x + width, y + height); // cạnh dưới
        gc.strokeLine(x + width, y, x + width, y + height); // cạnh phải
    }

    /** Vẽ các vết nứt tùy theo mức độ */
    private void drawCrack(GraphicsContext gc) {
        if (!cracked) return;

        gc.setStroke(Color.rgb(40, 40, 40, 0.9));
        gc.setLineWidth(1.2);

        // Vết nứt chính
        gc.strokeLine(x + width * 0.2, y + height * 0.2,
                x + width * 0.8, y + height * 0.8);

        if (heavilyCracked) {
            gc.strokeLine(x + width * 0.5, y + height * 0.1,
                    x + width * 0.6, y + height * 0.6);
            gc.strokeLine(x + width * 0.3, y + height * 0.5,
                    x + width * 0.7, y + height * 0.9);
        }
    }
}

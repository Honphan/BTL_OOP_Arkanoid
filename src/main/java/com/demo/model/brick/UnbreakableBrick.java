package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

public class UnbreakableBrick extends Brick {

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, "unbreakable");
    }

    @Override
    public void update() {
        // Gạch không di chuyển
    }

    @Override
    public void render(GraphicsContext gc) {
        if (getDestroyed()) return;

        drawBrickBody(gc);
        drawEdgeLight(gc);
    }

    @Override
    public void takeHit() {
        // Không làm gì — gạch không thể phá
    }

    /** Vẽ phần thân gạch xám kim loại với gradient sáng trên - tối dưới */
    private void drawBrickBody(GraphicsContext gc) {
        Color baseColor = Color.web("#555555");

        var gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, baseColor.brighter()),
                new Stop(0.5, baseColor),
                new Stop(1.0, baseColor.darker())
        );

        gc.setFill(gradient);
        gc.fillRect(x, y, width, height);

        // Viền bạc mờ
        gc.setStroke(Color.SILVER);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }

    /** Hiệu ứng sáng trên - tối dưới giống NormalBrick */
    private void drawEdgeLight(GraphicsContext gc) {
        // Cạnh trên & trái sáng
        gc.setStroke(Color.rgb(255, 255, 255, 0.4));
        gc.strokeLine(x, y, x + width, y);
        gc.strokeLine(x, y, x, y + height);

        // Cạnh dưới & phải tối
        gc.setStroke(Color.rgb(0, 0, 0, 0.6));
        gc.strokeLine(x, y + height, x + width, y + height);
        gc.strokeLine(x + width, y, x + width, y + height);
    }
}

package com.demo.Utils;

import com.demo.model.Ball;
import com.demo.model.Paddle;
import com.demo.model.Level;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class CanvasUtil {

    /**
     * Draws a debug grid on the canvas to visualize its bounds and center
     */
    public static void drawDebugGrid(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);

        // Draw outline
        gc.strokeRect(0, 0, width, height);

        // Draw diagonal lines
        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);

        // Draw center lines
        gc.strokeLine(width / 2, 0, width / 2, height);
        gc.strokeLine(0, height / 2, width, height / 2);
    }

    /**
     * Binds the canvas size to its parent container and handles resize events
     */
    public static void bindCanvasSize(Canvas gameCanvas, Pane gameArea,
                                      Paddle paddle,
                                      Runnable redrawAction,
                                      Consumer<Integer> levelInitializer,
                                      int currentLevel) {
        // Set initial canvas size to avoid zero dimensions
        if (gameArea.getWidth() <= 0) {
            gameCanvas.setWidth(600);
            gameCanvas.setHeight(400);
        } else {
            gameCanvas.setWidth(gameArea.getWidth());
            gameCanvas.setHeight(gameArea.getHeight());
        }

        // Bind canvas dimensions to parent container
        gameCanvas.widthProperty().bind(gameArea.widthProperty());
        gameCanvas.heightProperty().bind(gameArea.heightProperty());

        // Width change listener
        gameCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double width = newVal.doubleValue();

                // If oldVal is 0, center the paddle
                if (oldVal.doubleValue() <= 0) {
                    paddle.setX((width - paddle.getWidth()) / 2);
                } else {
                    // Keep paddle at relative position during resize
                    double relativePos = paddle.getX() / oldVal.doubleValue();
                    paddle.setX(relativePos * width);
                }

                // Redraw canvas when size changes
                redrawAction.run();
            }

            // Reinitialize level with new dimensions
            levelInitializer.accept(currentLevel);
        });

        // Height change listener
        gameCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double height = newVal.doubleValue();
                paddle.setY(height - paddle.getHeight() - 20);

                // Redraw canvas when size changes
                redrawAction.run();
            }
        });
    }

    public static void redrawCanvas(GraphicsContext gc, Canvas gameCanvas, Paddle paddle, Level level, Ball ball, Image backgroundImage) {
        // Vẽ background image thay vì màu đen
        Background.renderBackground(gc, gameCanvas.getWidth(), gameCanvas.getHeight(), backgroundImage);

        if (level != null) level.render(gc);
        if (paddle != null) paddle.render(gc);
        if (ball != null) ball.render(gc);
    }
}

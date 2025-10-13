package com.demo.Utils;

import com.demo.controller.GamePaneController;
import javafx.scene.input.KeyEvent;
import javafx.scene.canvas.Canvas;

public class InputHandle {
    private GamePaneController controller;
    private Canvas gameCanvas;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;

    public InputHandle(GamePaneController controller, Canvas gameCanvas) {
        this.controller = controller;
        this.gameCanvas = gameCanvas;
        setupKeyHandlers();
    }

    public void setupKeyHandlers() {
        // Đảm bảo canvas có thể nhận sự kiện bàn phím
        gameCanvas.setFocusTraversable(true);

        // Xử lý phím trực tiếp trên canvas
        gameCanvas.setOnKeyPressed(this::handleKeyPressed);
        gameCanvas.setOnKeyReleased(this::handleKeyReleased);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (controller.isGameRunning()) {
            switch (event.getCode()) {
                case LEFT:
                    leftKeyPressed = true;
                    event.consume();
                    break;
                case RIGHT:
                    rightKeyPressed = true;
                    event.consume();
                    break;
                case UP:
                case DOWN:
                    event.consume(); // Prevent focus traversal
                    break;
                default:
                    break;
            }
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        if (controller.isGameRunning()) {
            switch (event.getCode()) {
                case LEFT:
                    leftKeyPressed = false;
                    event.consume();
                    break;
                case RIGHT:
                    rightKeyPressed = false;
                    event.consume();
                    break;
                case UP:
                case DOWN:
                    event.consume(); // Prevent focus traversal
                    break;
                default:
                    break;
            }
        }
    }

    public boolean isLeftKeyPressed() {
        return leftKeyPressed;
    }

    public boolean isRightKeyPressed() {
        return rightKeyPressed;
    }

    public void requestCanvasFocus() {
        gameCanvas.requestFocus();
    }
}


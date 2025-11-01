package com.demo.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.canvas.Canvas;

public class InputHandle {
    private final GameControl controller;
    private final Canvas gameCanvas;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;

    public InputHandle(GameControl controller, Canvas gameCanvas) {
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
        // Luôn lắng nghe phím SPACE để bắt đầu game khi vào màn chơi
        if (event.getCode() == KeyCode.SPACE) {
            try {
                controller.startBySpace();
            } catch (Exception e) {
                // Không để crash nếu controller chưa sẵn sàng
            }
            event.consume();
            return; // SPACE đã được xử lý
        }

        // Các phím di chuyển chỉ có hiệu lực khi game đang chạy
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
        } else {
            // Khi game không chạy, vẫn ngăn UP/DOWN làm thay đổi focus
            switch (event.getCode()) {
                case UP:
                case DOWN:
                    event.consume();
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
        } else {
            // Ngăn UP/DOWN khi game không chạy
            switch (event.getCode()) {
                case UP:
                case DOWN:
                    event.consume();
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

package com.demo.controller;

import com.demo.model.Paddle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class gamePaneController {
    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private HBox livesContainer;

    @FXML
    private VBox gameStatusOverlay;

    @FXML
    private Label gameStatusLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private StackPane gameArea;

    private GraphicsContext gc;
    private Paddle paddle;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private AnimationTimer gameLoop;
    private boolean gameRunning = false;
    private boolean paddleInitialized = false;

    @FXML
    private void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        gameRunning = true;

        // Đặt màu nền cho Canvas để dễ nhìn
        gameCanvas.setStyle("-fx-background-color: black;");
        
        // Liên kết kích thước Canvas với StackPane chứa nó
        bindCanvasSize();
        
        // Thiết lập xử lý phím
        setupKeyHandlers();
        
        // Khởi tạo game loop
        setupGameLoop();
        
        // Khởi tạo paddle sau khi JavaFX đã render (đảm bảo kích thước canvas đã sẵn sàng)
        Platform.runLater(this::delayedInitialize);
    }
    
    private void delayedInitialize() {
        // In kích thước canvas để kiểm tra
        System.out.println("Canvas size after runLater: " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());
        
        // Tạm thời vẽ một khung để xác nhận canvas hoạt động
        drawDebugGrid();
        
        // Khởi tạo paddle
        initializePaddle();
        
        // Bắt đầu game loop
        gameLoop.start();
        
        // Đảm bảo canvas có focus
        gameCanvas.requestFocus();
    }

    private void drawDebugGrid() {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        
        double w = gameCanvas.getWidth();
        double h = gameCanvas.getHeight();
        
        // Vẽ đường viền
        gc.strokeRect(0, 0, w, h);
        
        // Vẽ đường chéo
        gc.strokeLine(0, 0, w, h);
        gc.strokeLine(0, h, w, 0);
        
        // Vẽ đường giữa
        gc.strokeLine(w/2, 0, w/2, h);
        gc.strokeLine(0, h/2, w, h/2);
    }

    /**
     * Liên kết kích thước Canvas với StackPane cha để co giãn theo khung hình
     */
    private void bindCanvasSize() {
        // Đặt kích thước ban đầu cho canvas (để tránh kích thước 0)
        if (gameArea.getWidth() <= 0) {
            gameCanvas.setWidth(600);
            gameCanvas.setHeight(400);
        } else {
            gameCanvas.setWidth(gameArea.getWidth());
            gameCanvas.setHeight(gameArea.getHeight());
        }
        
        // Bind chiều rộng và chiều cao của Canvas với StackPane
        gameCanvas.widthProperty().bind(gameArea.widthProperty());
        gameCanvas.heightProperty().bind(gameArea.heightProperty());

        // Lắng nghe thay đổi kích thước để cập nhật vị trí paddle
        gameCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double width = newVal.doubleValue();
                
                // Nếu oldVal là 0, đặt paddle ở giữa
                if (oldVal.doubleValue() <= 0) {
                    paddle.setX((width - paddle.getWidth()) / 2);
                } else {
                    // Giữ paddle ở vị trí tương đối khi resize
                    double relativePos = paddle.getX() / oldVal.doubleValue();
                    paddle.setX(relativePos * width);
                }
                
                // Vẽ lại paddle khi kích thước thay đổi
                redrawCanvas();
            }
        });

        gameCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double height = newVal.doubleValue();
                paddle.setY(height - paddle.getHeight() - 20);
                
                // Vẽ lại paddle khi kích thước thay đổi
                redrawCanvas();
            }
        });
    }

    /**
     * Khởi tạo paddle với vị trí và kích thước phù hợp
     */
    private void initializePaddle() {
        double paddleWidth = 100;
        double paddleHeight = 20;
        double paddleSpeed = 8;

        double canvasWidth = gameCanvas.getWidth();
        double canvasHeight = gameCanvas.getHeight();
        
        // In ra kích thước canvas khi khởi tạo paddle
        System.out.println("Initializing paddle with canvas size: " + canvasWidth + "x" + canvasHeight);
        
        // Đảm bảo canvas có kích thước hợp lệ
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            canvasWidth = 600; // Giá trị mặc định
            canvasHeight = 400; // Giá trị mặc định
            System.out.println("Using default canvas size: " + canvasWidth + "x" + canvasHeight);
        }
        
        double paddleX = (canvasWidth - paddleWidth) / 2;
        double paddleY = canvasHeight - paddleHeight - 20;

        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight, paddleSpeed);
        paddleInitialized = true;
        
        // Vẽ paddle ngay lập tức sau khi khởi tạo
        redrawCanvas();
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning && paddleInitialized) {
                    redrawCanvas();
                }
            }
        };
    }
    
    private void redrawCanvas() {
        // Xóa canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        
        // Vẽ khung debug nếu cần
        // drawDebugGrid();
        
        // Cập nhật trạng thái paddle
        updatePaddle();
        
        // Cập nhật vị trí paddle
        paddle.update();
        
        // Vẽ lại paddle
        paddle.render(gc);
    }

    private void setupKeyHandlers() {
        // Đảm bảo canvas có thể nhận sự kiện bàn phím
        gameCanvas.setFocusTraversable(true);

        // Xử lý phím trực tiếp trên canvas
        gameCanvas.setOnKeyPressed(this::handleKeyPressed);
        gameCanvas.setOnKeyReleased(this::handleKeyReleased);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (gameRunning) {
            switch (event.getCode()) {
                case LEFT:
                    leftKeyPressed = true;
                    event.consume();
                    break;
                case RIGHT:
                    rightKeyPressed = true;
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        if (gameRunning) {
            switch (event.getCode()) {
                case LEFT:
                    leftKeyPressed = false;
                    event.consume();
                    break;
                case RIGHT:
                    rightKeyPressed = false;
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }

    private void updatePaddle() {
        if (leftKeyPressed && !rightKeyPressed) {
            paddle.moveLeft();
        } else if (rightKeyPressed && !leftKeyPressed) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
    }

    @FXML
    public void startGame() {
        gameRunning = true;
        gameCanvas.requestFocus();
    }

    @FXML
    public void pauseGame() {
        gameRunning = false;
        gameCanvas.requestFocus();
    }

    @FXML
    public void restartGame() {
        // Reset vị trí paddle về giữa
        double paddleWidth = paddle.getWidth();
        paddle.setX((gameCanvas.getWidth() - paddleWidth) / 2);
        gameRunning = true;
        gameCanvas.requestFocus();
    }

    @FXML
    public void maxBet() {
        gameCanvas.requestFocus();
    }

    @FXML
    public void showPayTable() {
        gameCanvas.requestFocus();
    }
}
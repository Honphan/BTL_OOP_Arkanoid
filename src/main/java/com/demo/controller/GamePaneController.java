
package com.demo.controller;

import com.demo.Utils.Background;
import com.demo.Utils.CanvasUtil;
import com.demo.Utils.InputHandle;
import com.demo.Utils.SoundManager;
import com.demo.model.Ball;
import com.demo.model.Paddle;
import com.demo.model.Level;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GamePaneController {
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
    private Ball ball;
    private Paddle paddle;
    private Level level;
    private int currentLevel = 1;
    private AnimationTimer gameLoop;
    private boolean gameRunning = false;
    private boolean paddleInitialized = false;
    private Image backgroundImage;
    private SoundManager soundManager;
    private InputHandle inputHandle;



    @FXML
    private void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        gameRunning = true;
        // Load background image
        backgroundImage = Background.loadBackgroundImage();
        // Liên kết kích thước Canvas với StackPane chứa nó
        CanvasUtil.bindCanvasSize(
                gameCanvas,      // Canvas
                gameArea,        // Parent container
                paddle,          // Paddle object
                this::redrawCanvas,  // Redraw callback
                this::initializeLevel,// Level initializer callback
                currentLevel     // Current level
        );

        // Thiết lập xử lý phím
        inputHandle = new InputHandle(this, gameCanvas);
        // Khởi tạo game loop
        setupGameLoop();
        // Khởi tạo paddle sau khi JavaFX đã render
        Platform.runLater(this::delayedInitialize);
    }


    private void delayedInitialize() {
        // In kích thước canvas để kiểm tra
        System.out.println("Canvas size after runLater: " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());

        // Tạm thời vẽ một khung để xác nhận canvas hoạt động
        CanvasUtil.drawDebugGrid(gc, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Khởi tạo paddle
        initializePaddle();

        initializeLevel(currentLevel);

        initializeBall();

        // Bắt đầu game loop
        gameLoop.start();

        // Đảm bảo canvas có focus
        gameCanvas.requestFocus();
    }


    private void initializeBall() {
        double radius = 10; // Bán kính của bóng trong Ball.java
        double ballSpeed = 5;

        double startX = (gameCanvas.getWidth() / 2) - radius;
        double startY = gameCanvas.getHeight() - 2 * radius - 20;

        ball = new Ball(startX, startY, ballSpeed);
    }
    /**
     * Khởi tạo paddle với vị trí và kích thước phù hợp
     */
    private void initializePaddle() {
        double paddleWidth = 200;
        double paddleHeight = 20;
        double paddleSpeed = 5;
        double dx = 0;
        double dy = 0;

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

    private void initializeLevel(int levelNumber) {
        double canvasWidth = gameCanvas.getWidth();
        double totalWidth = canvasWidth * 0.8; // gạch chiếm 80% chiều ngang canvas
        double totalHeight = 100;              // chiều cao vùng chứa gạch
        double startX = (canvasWidth - totalWidth) / 2; // căn giữa
        double startY = 40;                    // cách đỉnh 50px

        level = new Level(levelNumber, startX, startY, totalWidth, totalHeight);
    }

    private void setupGameLoop() {
        final long[] lastFrameTime = {0};
        final double frameRate = 60.0; // Giới hạn 60 FPS
        final double nanosPerFrame = 1_000_000_000.0 / frameRate;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime[0] < nanosPerFrame) return;
                lastFrameTime[0] = now;

                if (gameRunning && paddleInitialized) {
                    updateGame();
                    redrawCanvas();
                }
            }
        };
    }

    public boolean isGameRunning() {
        return gameRunning;
    }


    private void updateGame() {
        updatePaddle();
        paddle.update(gameCanvas.getWidth());

        ball.update();
        ball.bounceOffWall(gameCanvas.getWidth(), gameCanvas.getHeight());

        if (ball.checkCollision(paddle)) {
            ball.bounceOffPaddle(paddle);
        }

        if (level != null && level.isCleared()) {
            currentLevel++;
            if (currentLevel > 3) {
                gameRunning = false;
            } else {
                initializeLevel(currentLevel);
            }
        }
    }


    private void redrawCanvas() {
            CanvasUtil.redrawCanvas(gc, gameCanvas, paddle, level, ball, backgroundImage);
    }

    private void updatePaddle() {
        // Use inputHandle instead of class fields
        if (inputHandle.isLeftKeyPressed() && !inputHandle.isRightKeyPressed()) {
            paddle.moveLeft();
        } else if (inputHandle.isRightKeyPressed() && !inputHandle.isLeftKeyPressed()) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
    }

    @FXML
    public void startGame() {
        gameRunning = true;
        inputHandle.requestCanvasFocus();
    }

    @FXML
    public void pauseGame() {
        gameRunning = false;
        inputHandle.requestCanvasFocus();
    }

    @FXML
    public void restartGame() {
        // Reset vị trí paddle về giữa
        double paddleWidth = paddle.getWidth();
        paddle.setX((gameCanvas.getWidth() - paddleWidth) / 2);
        gameRunning = true;
        inputHandle.requestCanvasFocus();
        ball.reset(gameCanvas.getWidth() / 2 - 10, gameCanvas.getHeight() - 10 - 20);
    }

    @FXML
    public void maxBet() {
        inputHandle.requestCanvasFocus();
    }

    @FXML
    public void showPayTable() {
        inputHandle.requestCanvasFocus();
    }
}


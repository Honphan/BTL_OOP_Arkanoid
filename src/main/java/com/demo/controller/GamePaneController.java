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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

        soundManager = SoundManager.getInstance();

        // Load background image
        backgroundImage = Background.loadBackgroundImage();

        // Bind canvas size
        CanvasUtil.bindCanvasSize(
                gameCanvas,
                gameArea,
                paddle,
                this::redrawCanvas,
                this::initializeLevel,
                currentLevel
        );

        // Setup input handling
        inputHandle = new InputHandle(this, gameCanvas);

        // Setup game loop
        setupGameLoop();

        // Initialize after JavaFX rendering
        Platform.runLater(this::delayedInitialize);
    }

    /**
     * Set the starting level (called from Render)
     */
    public void setStartingLevel(int levelNumber) {
        this.currentLevel = levelNumber;
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(levelNumber));
        }
    }

    private void delayedInitialize() {
        System.out.println("Canvas size after runLater: " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());

        CanvasUtil.drawDebugGrid(gc, gameCanvas.getWidth(), gameCanvas.getHeight());

        initializePaddle();
        initializeLevel(currentLevel);
        initializeBall();

        // Update level label
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(currentLevel));
        }

        gameLoop.start();
        gameCanvas.requestFocus();
    }

    private void initializeBall() {
        double radius = 10;
        double ballSpeed = 5;

        double startX = (gameCanvas.getWidth() / 2) - radius;
        double startY = gameCanvas.getHeight() - 2 * radius - 20;

        ball = new Ball(startX, startY, ballSpeed);
    }

    private void initializePaddle() {
        double paddleWidth = 200;
        double paddleHeight = 20;
        double paddleSpeed = 5;

        double canvasWidth = gameCanvas.getWidth();
        double canvasHeight = gameCanvas.getHeight();

        System.out.println("Initializing paddle with canvas size: " + canvasWidth + "x" + canvasHeight);

        if (canvasWidth <= 0 || canvasHeight <= 0) {
            canvasWidth = 600;
            canvasHeight = 400;
            System.out.println("Using default canvas size: " + canvasWidth + "x" + canvasHeight);
        }

        double paddleX = (canvasWidth - paddleWidth) / 2;
        double paddleY = canvasHeight - paddleHeight - 20;

        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight, paddleSpeed);
        paddleInitialized = true;

        redrawCanvas();
    }

    private void initializeLevel(int levelNumber) {
        double canvasWidth = gameCanvas.getWidth();
        double totalWidth = canvasWidth * 0.8;
        double totalHeight = 100;
        double startX = (canvasWidth - totalWidth) / 2;
        double startY = 40;

        level = new Level(levelNumber, startX, startY, totalWidth, totalHeight);

        // Update level label
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(levelNumber));
        }
    }

    private void setupGameLoop() {
        final long[] lastFrameTime = {0};
        final double frameRate = 60.0;
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
                showGameOver("YOU WIN!");
            } else {
                initializeLevel(currentLevel);
            }
        }
    }

    private void showGameOver(String message) {
        if (gameStatusLabel != null && gameStatusOverlay != null) {
            gameStatusLabel.setText(message);
            gameStatusOverlay.setVisible(true);
        }
    }

    private void redrawCanvas() {
        CanvasUtil.redrawCanvas(gc, gameCanvas, paddle, level, ball, backgroundImage);
    }

    private void updatePaddle() {
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
        if (gameStatusOverlay != null) {
            gameStatusOverlay.setVisible(false);
        }
        inputHandle.requestCanvasFocus();
    }

    @FXML
    public void pauseGame() {
        gameRunning = false;
        inputHandle.requestCanvasFocus();
    }

    @FXML
    public void restartGame() {
        // Reset to starting level
        currentLevel = 1;
        initializeLevel(currentLevel);

        // Reset paddle position
        double paddleWidth = paddle.getWidth();
        paddle.setX((gameCanvas.getWidth() - paddleWidth) / 2);

        // Reset ball
        ball.reset(gameCanvas.getWidth() / 2 - 10, gameCanvas.getHeight() - 10 - 20);

        gameRunning = true;
        if (gameStatusOverlay != null) {
            gameStatusOverlay.setVisible(false);
        }
        inputHandle.requestCanvasFocus();
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
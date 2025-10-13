package com.demo.controller;

import com.demo.Utils.Background;
import com.demo.Utils.CanvasUtil;
import com.demo.Utils.InputHandle;
import com.demo.Utils.SoundManager;
import com.demo.model.Ball;
import com.demo.model.Paddle;
import com.demo.model.bricks.*;
import com.demo.model.powerup.*;
import com.demo.model.LevelManager;
import com.demo.model.PowerUpManager;
import com.demo.model.bricks.PowerUpBrick;
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
    private LevelManager level;
    private int currentLevel = 1;
    private PowerUpManager powerup;
    private int score = 0;
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
                this::initializeLevelandPowerUp,
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
        initializeLevelandPowerUp(currentLevel);
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
        double ballSpeed = 8;

        double startX = (gameCanvas.getWidth() / 2) - radius;
        double startY = gameCanvas.getHeight() - 2 * radius - 20;

        ball = new Ball(startX, startY, ballSpeed);
    }

    private void initializePaddle() {
        double paddleWidth = 200;
        double paddleHeight = 20;
        double paddleSpeed = 15;

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

    private void initializeLevelandPowerUp(int levelNumber) {
        double totalWidth = gameCanvas.getWidth();
        double totalHeight = gameCanvas.getHeight();

        level = new LevelManager(levelNumber, totalWidth, totalHeight);
        powerup = new PowerUpManager();

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

        if (level != null) {
            Brick[][] bricks = level.getBricks(); // lấy mảng 2 chiều gạch
            int rows = level.getRows();
            int cols = level.getCols();

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    Brick brick = bricks[r][c];
                    if (brick != null && !brick.isDestroyed()) {
                        if (ball.checkCollision(brick)) {
                            ball.bounceOff(brick);
                            brick.takeHit();
                            if (brick.isDestroyed() && brick instanceof PowerUpBrick powerbrick) {
                                PowerUp newPU = powerbrick.spawnPowerUp();
                                if (newPU != null) {
                                    powerup.addPowerUp(newPU);
                                }
                                addScore(brick.getScoreValue());
                            }
                        }
                    }
                }
            }
        }

        if (powerup != null) {
            powerup.update(gameCanvas.getHeight());
        }

        if (level != null && level.isCleared()) {
            currentLevel++;
            if (currentLevel > 3) {
                gameRunning = false;
            } else {
                initializeLevelandPowerUp(currentLevel);
            }
        }
    }

    private void addScore(int points) {
        score += points;
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(score));
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

        if (powerup != null) {
            powerup.render(gc);
        }
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
        // Reset game state to beginning
        score = 0;
        currentLevel = 1;

        // Update displays
        updateScoreDisplay();
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(currentLevel));
        }

        // Reinitialize game components
        initializeLevelandPowerUp(currentLevel);

        // Reset paddle position
        if (paddle != null) {
            double paddleWidth = paddle.getWidth();
            paddle.setX((gameCanvas.getWidth() - paddleWidth) / 2);
        }

        // Reset ball position
        if (ball != null) {
            ball.reset(gameCanvas.getWidth() / 2 - 10, gameCanvas.getHeight() - 10 - 20);
        }

        // Reset power-ups
        if (powerup != null) {
            powerup = new PowerUpManager();
        }

        // Start the game
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
        score = 0;
        currentLevel = 1;
        initializeLevelandPowerUp(currentLevel);

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
package com.demo.controller;

import com.demo.GameView.Render;
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
import com.demo.model.score.ScoreManager;
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
    private Label highScoreLabel;


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
    private ScoreManager scoreManager;


    @FXML
    private void initialize() {
        this.currentLevel = Render.getCurrentLevel();
        gc = gameCanvas.getGraphicsContext2D();
        System.out.println(gameArea.getWidth());
        System.out.println(gameArea.getHeight());

        gameRunning = true;

        soundManager = SoundManager.getInstance();

        // Khởi tạo ScoreManager
        scoreManager = ScoreManager.getInstance();

        // Hiển thị điểm cao ban đầu
        updateHighScoreDisplay();


        // Load background image
        backgroundImage = Background.loadBackgroundImage();

        System.out.println(currentLevel);

        // Bind canvas size
        CanvasUtil.bindCanvasSize(
                gameCanvas,
                gameArea,
                paddle,
                this::redrawCanvas,
                this::initializeLevelandPowerUp,
                currentLevel
        );

        gameCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double width = newVal.doubleValue();
                System.out.println(width);
                // If oldVal is 0, center the paddle
                if (oldVal.doubleValue() <= 0) {
                    paddle.setX((width - paddle.getWidth()) / 2);
                } else {
                    // Keep paddle at relative position during resize
                    double relativePos = paddle.getX() / oldVal.doubleValue();
                    paddle.setX(relativePos * width);
                }

                // Redraw canvas when size changes
                redrawCanvas();
            }

        });

        gameCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (paddle != null) {
                double height = newVal.doubleValue();
                paddle.setY(height - paddle.getHeight() - 20);

                // Redraw canvas when size changes
                redrawCanvas();
            }
        });

        System.out.println("After bindCanvasize");
        System.out.println(gc.getCanvas().getWidth());
        System.out.println(gc.getCanvas().getHeight());

        // Setup input handling
        inputHandle = new InputHandle(this, gameCanvas);

        // Setup game loop
        setupGameLoop();

        // Initialize after JavaFX rendering
        Platform.runLater(this::delayedInitialize);
    }


    private void delayedInitialize() {
        System.out.println("Canvas size after runLater: " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());
        System.out.println("Game area size after runLater: " + gameArea.getWidth() + "x" + gameArea.getHeight());

        CanvasUtil.drawDebugGrid(gc, gameCanvas.getWidth(), gameCanvas.getHeight());
        initializePaddle();
        initializeLevelandPowerUp(currentLevel);
        initializeBall();
        updateScoreDisplay();
        updateLevelDisplay();

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

        double canvasWidth = gameArea.getWidth();
        double canvasHeight = gameArea.getHeight();

        System.out.println("Initializing paddle with canvas size: " + canvasWidth + "x" + canvasHeight);

        if (canvasWidth <= 0 || canvasHeight <= 0) {
            canvasWidth = 600;
            canvasHeight = 400;
            System.out.println("Using default canvas size: " + canvasWidth + "x" + canvasHeight);
        }

        double paddleX = (canvasWidth - paddleWidth) / 2;
        double paddleY = canvasHeight * 0.9;

        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight, paddleSpeed);
        paddleInitialized = true;
        redrawCanvas();
    }

    private void initializeLevelandPowerUp(int levelNumber) {
        System.out.println("Initializing level " + levelNumber);
        double totalWidth = gameCanvas.getWidth();
        double totalHeight = gameCanvas.getHeight();

        level = new LevelManager(levelNumber, totalWidth, totalHeight);
        powerup = new PowerUpManager();

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
            // Lưu điểm cao của level hiện tại trước khi chuyển level
            scoreManager.updateHighScore(currentLevel, score);

            currentLevel++;
            if (currentLevel > 3) {
                gameRunning = false;
                showGameOver("Bạn đã hoàn thành tất cả các level!");
            } else {
                initializeLevelandPowerUp(currentLevel);
                // Cập nhật hiển thị điểm cao cho level mới
                updateHighScoreDisplay();
            }
        }

    }

    private void addScore(int points) {
        score += points;
        updateScoreDisplay();

        // Kiểm tra và cập nhật điểm cao nếu cần
        boolean isNewHighScore = scoreManager.updateHighScore(currentLevel, score);
        if (isNewHighScore) {
            updateHighScoreDisplay();
//            // Có thể thêm hiệu ứng đặc biệt khi đạt điểm cao mới
//            showNewHighScoreAnimation();
        }
    }

    // Phương thức hiển thị hiệu ứng đạt điểm cao mới
    private void showNewHighScoreAnimation() {
        // Hiệu ứng khi đạt điểm cao mới, ví dụ:
        // - Làm sáng label điểm cao
        // - Phát âm thanh đặc biệt
        highScoreLabel.setStyle("-fx-text-fill: gold;");

        // Sau 2 giây, trở về bình thường
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> highScoreLabel.setStyle("-fx-text-fill: white;"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void updateScoreDisplay() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(score));
        }
    }

    private void updateLevelDisplay() {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(currentLevel));
            System.out.println("Updating level label to: " + levelLabel.getText());
        }
    }

    private void updateHighScoreDisplay() {
        if (highScoreLabel != null) {
            int highScore = scoreManager.getHighScore(currentLevel);
            highScoreLabel.setText(String.valueOf(highScore));
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
    public void ReStartGame() {
        // Reset game state to beginning
        score = 0;
        System.out.println("Canvas size after startGame : " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());
        System.out.println("Game area size after startGame: " + gameArea.getWidth() + "x" + gameArea.getHeight());
        // Update displays
        updateScoreDisplay();
        updateLevelDisplay();
        updateHighScoreDisplay();

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

        // Update displays
        updateScoreDisplay();
        updateLevelDisplay();
        updateHighScoreDisplay();

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
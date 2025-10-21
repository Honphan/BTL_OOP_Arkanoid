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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
    private int lives = 3; // Số mạng ban đầu
    private List<Ball> balls = new java.util.ArrayList<>();



    @FXML
    private void initialize() {
        this.currentLevel = Render.getCurrentLevel();
        lives = 3;
        updateLivesDisplay();
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
        balls.clear();
        balls.add(ball);
    }

    private void createMultiBalls(Ball originalBall) {
        double radius = 10;

        Ball ball1 = new Ball(originalBall.getX(), originalBall.getY(), originalBall.getSpeed());
        Ball ball2 = new Ball(originalBall.getX(), originalBall.getY(), originalBall.getSpeed());

        // Lấy vận tốc hiện tại của bóng gốc
        double originalDx = originalBall.getDx();
        double originalDy = originalBall.getDy();

        // Tính góc hiện tại của bóng gốc
        double currentAngle = Math.atan2(originalDy, originalDx);

        // Tạo 2 góc lệch 30 độ sang 2 bên
        double angle1 = currentAngle - Math.toRadians(30);
        double angle2 = currentAngle + Math.toRadians(30);

        double speed = originalBall.getSpeed();

        // Set vận tốc cho bóng 1 (lệch trái)
        ball1.setDx(Math.cos(angle1) * speed);
        ball1.setDy(Math.sin(angle1) * speed);

        // Set vận tốc cho bóng 2 (lệch phải)
        ball2.setDx(Math.cos(angle2) * speed);
        ball2.setDy(Math.sin(angle2) * speed);

        balls.add(ball1);
        balls.add(ball2);

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

        // CẬP NHẬT TẤT CẢ BÓNG
        java.util.Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball currentBall = ballIterator.next();

            currentBall.update();
            currentBall.bounceOffWall(gameCanvas.getWidth(), gameCanvas.getHeight());

            if (currentBall.checkCollision(paddle)) {
                currentBall.bounceOffPaddle(paddle);
            }

            // Kiểm tra bóng rơi ra ngoài
            if (currentBall.getY() > gameCanvas.getHeight()) {
                ballIterator.remove();
                // Nếu hết bóng → Game Over
                if (balls.isEmpty()) {
                    gameRunning = false;
                    showGameOver("GAME OVER!");
                    return;
                }
            }

            // KIỂM TRA VA CHẠM VỚI GẠCH
            if (level != null) {
                Brick[][] bricks = level.getBricks();
                int rows = level.getRows();
                int cols = level.getCols();

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        Brick brick = bricks[r][c];
                        if (brick != null && !brick.isDestroyed()) {
                            if (currentBall.checkCollision(brick)) {
                                currentBall.bounceOff(brick);
                                brick.takeHit();
                                if (brick.isDestroyed()) {
                                    addScore(brick.getScoreValue());

                                    // Spawn power-up nếu là PowerUpBrick
                                    if (brick instanceof PowerUpBrick powerbrick) {
                                        PowerUp newPU = powerbrick.spawnPowerUp();
                                        if (newPU != null) {
                                            powerup.addPowerUp(newPU);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ⚡ KIỂM TRA VA CHẠM GIỮA CÁC BÓNG VỚI NHAU
        checkBallCollisions();

        // CẬP NHẬT POWER-UP
        if (powerup != null) {
            powerup.update(gameCanvas.getHeight());
            checkPowerUpCollision();
        }

        // KIỂM TRA HOÀN THÀNH LEVEL
        if (level != null && level.isCleared()) {
            currentLevel++;
            if (currentLevel > 3) {
                gameRunning = false;
                showGameOver("YOU WIN!");
            } else {
                initializeLevelandPowerUp(currentLevel);
            }
        }
    }
    private void loseLife() {
        lives--;
        updateLivesDisplay(); // Cập nhật hiển thị số mạng

        // Phát âm thanh khi mất mạng (nếu có)
        if (soundManager != null) {
            soundManager.playSound("loseLife");
        }

        // Nếu hết mạng thì kết thúc trò chơi
        if (lives <= 0) {
            gameRunning = false;
            showGameOver("GAME OVER");
        }
    }

    private void updateLivesDisplay() {
        // Xóa tất cả hiển thị mạng hiện tại
        livesContainer.getChildren().clear();

        // Thêm lại số mạng còn lại
        for (int i = 0; i < lives; i++) {
            Label heartLabel = new Label("♥");
            heartLabel.getStyleClass().add("heart");
            livesContainer.getChildren().add(heartLabel);
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

        for (Ball currentBall : balls) {
            if (currentBall != null) {
                currentBall.render(gc);
            }
        }
        if (powerup != null) {
            powerup.render(gc);
        }
    }

    private void updatePaddle() {
        if (paddle.getCurrentPowerUp() != null && System.currentTimeMillis() >= paddle.getPowerUpEndTime()) {
            paddle.removePowerUp();
        }
        if (inputHandle.isLeftKeyPressed() && !inputHandle.isRightKeyPressed()) {
            paddle.moveLeft();
        } else if (inputHandle.isRightKeyPressed() && !inputHandle.isLeftKeyPressed()) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
    }

    private void checkPowerUpCollision() {
        if (powerup == null || paddle == null) return;

        java.util.List<PowerUp> powerUpList = powerup.getPowerUps();
        java.util.Iterator<PowerUp> iterator = powerUpList.iterator();

        while (iterator.hasNext()) {
            PowerUp pu = iterator.next();

            if (!pu.isActive()) {
                iterator.remove();
                continue;
            }

            // Kiểm tra va chạm: power-up chạm paddle
            if (checkCollision(pu, paddle)) {
                // Xử lý đặc biệt cho Multi Ball
                if (pu.getType() == PowerUpType.MULTI_BALL) {
                    // Tạo thêm 2 bóng mới từ bóng chính
                    if (!balls.isEmpty()) {
                        createMultiBalls(balls.get(0)); // Dùng bóng đầu tiên làm gốc
                    }
                } else {
                    // Áp dụng power-up bình thường cho paddle
                    paddle.applyPowerUp(pu);
                }

                // Vô hiệu hóa power-up
                pu.setActive(false);
                iterator.remove();

            }


            // Xóa power-up nếu rơi ra ngoài màn hình
            if (pu.getY() > gameCanvas.getHeight()) {
                pu.setActive(false);
                iterator.remove();
            }
        }
    }

    // Thêm method helper để kiểm tra va chạm:
    private boolean checkCollision(PowerUp powerUp, Paddle paddle) {
        double puX = powerUp.getX();
        double puY = powerUp.getY();
        double puWidth = powerUp.getWidth();
        double puHeight = powerUp.getHeight();

        double pX = paddle.getX();
        double pY = paddle.getY();
        double pWidth = paddle.getWidth();
        double pHeight = paddle.getHeight();

        // Kiểm tra hình chữ nhật có giao nhau không
        return puX < pX + pWidth &&
                puX + puWidth > pX &&
                puY < pY + pHeight &&
                puY + puHeight > pY;
    }

    // Kiểm tra va chạm giữa các bóng với nhau
    private void checkBallCollisions() {
        // Duyệt qua tất cả các cặp bóng
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball1 = balls.get(i);
                Ball ball2 = balls.get(j);

                // Kiểm tra va chạm giữa 2 bóng
                if (ball1.checkCollisionWithBall(ball2)) {
                    // Xử lý bật bóng
                    ball1.bounceOffBall(ball2);
                }
            }
        }
    }


  @FXML
    public void startGame() {
        currentLevel = 1;
        score = 0;
        System.out.println("Canvas size after startGame : " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());
        System.out.println("Game area size after startGame: "                                      + gameArea.getWidth() + "x" + gameArea.getHeight());
        // Update displays
        updateScoreDisplay();
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(currentLevel));
        }

        // Reinitialize game components
        initializeLevelandPowerUp(currentLevel);
            if (paddle != null) {
            double paddleWidth = paddle.getWidth();
            paddle.setX((gameCanvas.getWidth() - paddleWidth) / 2);
        }

        // Reset ball position
        if (ball != null) {
            ball.reset(gameCanvas.getWidth() / 2 - 10, gameCanvas.getHeight() - 10 - 20);
            balls.clear();
            balls.add(ball);
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
        if (gameRunning) {
            // Nếu game đang chạy, dừng lại và đổi nút thành "Continue"
            gameRunning = false;
            pauseButton.setText("CONTINUE");
        } else {
            // Nếu game đang dừng, tiếp tục chạy và đổi nút thành "Pause"
            gameRunning = true;
            pauseButton.setText("PAUSE");
        }

        inputHandle.requestCanvasFocus();

    }

    @FXML
    public void restartGame() {
        // Reset to starting level
        score = 0;
        currentLevel = 1;
        lives = 3;
        updateLivesDisplay();
        initializeLevelandPowerUp(currentLevel);

        // Update displays
        updateScoreDisplay();
        updateLevelDisplay();
        updateHighScoreDisplay();
        updateLevelDisplay();

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
    public void returnGame(ActionEvent event) throws IOException {
        // Stop game loop
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Save high score before returning
        if (scoreManager != null) {
            scoreManager.updateHighScore(currentLevel, score);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get current window size
        double windowWidth = stage.getWidth();
        double windowHeight = stage.getHeight();

        // Load the menu view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/demo/view/fxml/menu.fxml"));
        Parent root = loader.load();

        // Create a scene with the same dimensions
        Scene scene = new Scene(root, windowWidth, windowHeight);

        // Play menu background music
        if (soundManager != null) {
            soundManager.stopBackgroundMusic();
            soundManager.playBackgroundMusic("/com/demo/view/sound/barkanoid_background.mp3");
        }

        // Set the scene and show
        stage.setScene(scene);
        stage.show();

        inputHandle.requestCanvasFocus();
    }

}
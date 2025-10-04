package com.demo.model;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class GameManager {

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
    private AnchorPane gameArea;

    private GraphicsContext gc;
    private Paddle paddle;
    private Brick[][] bricks;
    private int currentLevel = 3;
    private int[][] LEVEL1 = {
            {1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1},
            {1, 1, 1, 1, 1}
    };

    private int[][] LEVEL2 = {
            {2, 1, 2, 1, 2, 1},
            {1, 2, 1, 2, 1, 2},
            {2, 1, 2, 1, 2, 1},
            {1, 2, 1, 2, 1, 2}
    };

    private int[][] LEVEL3 = {
            {-1, 3, -1, 3, -1, 3, -1},
            {3, 2, 3, 2, 3, 2, 3},
            {2, 1, 2, 1, 2, 1, 2},
            {1, 1, 1, 1, 1, 1, 1},
            {3, 2, 3, 2, 3, 2, 3}
    };
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private AnimationTimer gameLoop;
    private boolean gameRunning = false;



    public GameManager() {

    }
    @FXML
    private void initialize() {
        gc = gameCanvas.getGraphicsContext2D();

        // Khởi tạo paddle ở giữa màn hình, dưới cùng
        double paddleWidth = 200;
        double paddleHeight = 20;
        double paddleX = (gameCanvas.getWidth() - paddleWidth) / 2;
        double paddleY = gameCanvas.getHeight() - paddleHeight - 10;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight, gameCanvas.getWidth());
        paddle.draw(gc);

        loadLevel(currentLevel);
    }

    private void loadLevel(int level) {
        int[][] layout;
        switch (level) {
            case 1 -> layout = LEVEL1;
            case 2 -> layout = LEVEL2;
            case 3 -> layout = LEVEL3;
            default -> layout = LEVEL1;
        }

        gameArea.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Rectangle);

        int row = layout.length;
        int col = layout[0].length;

        double brickWidth = 50;
        double brickHeight = 15;
        double totalWidth = col * brickWidth + col * 5;
        double startX = (gameCanvas.getWidth() - totalWidth) / 2;
        double startY = 80;

        bricks = new Brick[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int val = layout[i][j];
                if (val != 0) {
                    String type = "";
                    int hitPoints = 0;
                    if (val == -1) {
                        type = "unbreakable";
                        hitPoints = Integer.MAX_VALUE;
                    } else {
                        type = "normal";
                        hitPoints = val;
                    }

                    double x = startX + j * (brickWidth + 5);
                    double y = startY + i * (brickHeight + 5);

                    Brick brick = new Brick((int) x, (int) y, (int) brickWidth, (int) brickHeight, hitPoints, type);
                    brick.updateColor();
                    bricks[i][j] = brick;
                    gameArea.getChildren().add(brick.node);
                }
            }
        }
    }

    @FXML
    public void startGame() {}
    @FXML
    public void pauseGame() {}
    @FXML
    public void restartGame() {
        loadLevel(currentLevel);
    }
    @FXML
    public void maxBet() {}
    @FXML
    public void showPayTable() {}
}

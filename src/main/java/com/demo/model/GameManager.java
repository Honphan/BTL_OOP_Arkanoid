package com.demo.model;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

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

    private GraphicsContext gc;
    private Paddle paddle;
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
    }

    @FXML
    public void startGame(){}
    @FXML
    public void pauseGame(){}
    @FXML
    public void restartGame(){}
    @FXML
    public void maxBet(){}
    @FXML
    public void showPayTable(){}
}

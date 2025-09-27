package com.demo.model;

import java.util.List;

public class GameManager {
    private static GameManager gameManager;
    public static GameManager getInstance() {
        if (gameManager == null) {
            gameManager = new GameManager();
        }
        return gameManager;
    }

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks ;
    private List<PowerUp> powerUps;
    private int score;
    private int lifes;


    private GameManager() {
    }
    public void startGame(){}
    public void pauseGame(){}
    public void endGame(){}
    public void update(){}
    public void checkCollision(){}
}

package com.demo.model;

import javafx.scene.canvas.GraphicsContext;

public class Level {
    private Brick[][] bricks;
    private int currentLevel;
    private int rows;
    private int cols;
    private double brickWidth;
    private double brickHeight;
    private double spacing = 5; // khoảng cách giữa các viên gạch

    public static final int MAX_LEVEL_BRICK = 3;

    public static final int[][] LEVEL1 = {
            {-1, 1, 0, 2, -1, 1},
            {2, 0, -1, 3, 0, 2},
            {1, 3, 0, -1, 1, 0}
    };
    public static final int[][] LEVEL2 = {
            {0, 2, -1, 1, 3, -1},
            {-1, 0, 2, 1, 0, 2},
            {2, 1, 0, -1, -1, 3}
    };
    public static final int[][] LEVEL3 = {
            {1, -1, 3, 2, 3, 1},
            {2, 1, -1, 0, -1, 2},
            {0, 2, 1, -1, 3, 0}
    };


    public Level(int currentLevel, double startX, double startY, double totalWidth, double totalHeight) {
        this.currentLevel = currentLevel;

        int[][] layout = layoutByLevel(currentLevel);
        this.rows = layout.length;
        this.cols = layout[0].length;

        this.brickWidth = (totalWidth - (cols - 1) * spacing) / cols;
        this.brickHeight = (totalHeight - (rows - 1) * spacing) / rows;

        bricks = new Brick[rows][cols];
        createBricks(layout, startX, startY);
    }

    private int[][] layoutByLevel(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> LEVEL1;
            case 2 -> LEVEL2;
            case 3 -> LEVEL3;
            default -> LEVEL1;
        };
    }

    private void createBricks(int[][] layout, double startX, double startY) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int value = layout[r][c];
                if (value != 0) {
                    double x = startX + c * (brickWidth + spacing);
                    double y = startY + r * (brickHeight + spacing);

                    String type = (value == -1) ? "unbreakable" : "normal";
                    int hitPoints = (type.equals("unbreakable")) ? Integer.MAX_VALUE : value;

                    bricks[r][c] = new Brick(x, y, brickWidth, brickHeight, hitPoints, type);
                } else {
                    bricks[r][c] = null;
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !brick.isDestroyed()) {
                    brick.render(gc);
                }
            }
        }
    }

    public Level nexLevel(double startX, double startY, double totalWidth, double totalHeight) {
        int next = currentLevel++;
        if (currentLevel > MAX_LEVEL_BRICK) {
            currentLevel = 1;
        }
        return new Level(next, startX, startY, totalWidth, totalHeight);
    }


    public boolean isCleared() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !"unbreakable".equals(brick.getType()) && !brick.isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Brick[][] getBricks() { return bricks; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public double getBrickWidth() { return brickWidth; }
    public double getBrickHeight() { return brickHeight; }
    public int getCurrentLevel() { return currentLevel; }



}

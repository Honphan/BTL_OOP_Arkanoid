package com.demo.model;

import com.demo.model.bricks.*;
import javafx.scene.canvas.GraphicsContext;

public class LevelManager {

    private Brick[][] bricks;
    private int currentLevel;
    private int row;
    private int col;
    private double brickWidth;
    private double brickHeight;
    private double spacing = 5;

    public static final int MAX_LEVEL_BRICK = 3;

    // -1: Unbreakable | 0: trống | >0: số hit của Normal
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

    public LevelManager(int currentLevel, double startX, double startY, double totalWidth, double totalHeight) {
        this.currentLevel = currentLevel;
        int[][] layout = layoutByLevel(currentLevel);

        this.row = layout.length;
        this.col = layout[0].length;

        this.brickWidth = (totalWidth - (col - 1) * spacing) / col;
        this.brickHeight = (totalHeight - (row - 1) * spacing) / row;

        bricks = new Brick[row][col];
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
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int value = layout[r][c];
                if (value == 0) {
                    bricks[r][c] = null;
                    continue;
                }

                double x = startX + c * (brickWidth + spacing);
                double y = startY + r * (brickHeight + spacing);

                Brick brick;
                if (value == -1) {
                    brick = new UnbreakableBrick(x, y, brickWidth, brickHeight);
                } else {
                    brick = new NormalBrick(x, y, brickWidth, brickHeight, value);
                }

                bricks[r][c] = brick;
            }
        }
    }

    public LevelManager nextLevel(double startX, double startY, double totalWidth, double totalHeight) {
        currentLevel++;
        if (currentLevel > MAX_LEVEL_BRICK) {
            currentLevel = 1;
        }
        return new LevelManager(currentLevel, startX, startY, totalWidth, totalHeight);
    }

    public void render(GraphicsContext gc) {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !brick.isDestroyed()) {
                    brick.render(gc);
                }
            }
        }
    }

    public boolean isCleared() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !(brick instanceof UnbreakableBrick) && !brick.isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getter
    public Brick[][] getBricks() { return bricks; }
    public int getRows() { return row; }
    public int getCols() { return col; }
    public double getBrickWidth() { return brickWidth; }
    public double getBrickHeight() { return brickHeight; }
    public int getCurrentLevel() { return currentLevel; }
}
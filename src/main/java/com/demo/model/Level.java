package com.demo.model;

import com.demo.model.bricks.Brick;
import com.demo.model.bricks.NormalBrick;
import com.demo.model.bricks.UnbreakableBrick;
import javafx.scene.canvas.GraphicsContext;

public class Level {
    // Mảng lưu trữ các viên gạch của level
    private Brick[][] bricks;
    private int currentLevel;
    private int row;
    private int col;
    private double brickWidth;
    private double brickHeight;
    private double spacing = 5;

    public static final int MAX_LEVEL_BRICK = 3; // số level tối đa

    // Layout các level, -1 = unbreakable, 0 = trống, >0 = hit points của gạch
    public static final int[][] LEVEL1 = {
            { 0,  3, -1,  1,  0,  2, -1,  5,  0,  1},
            { 2, -1,  0,  4,  1,  0,  3,  0, -1,  2},
            { 0,  1,  2, -1,  0,  3,  1,  0,  4, -1},
            { 3,  0, -1,  2,  0,  1,  0,  5,  2,  0},
            { 1,  4,  0, -1,  3,  0, -1,  2,  0,  3},
            { 0, -1,  2,  0,  5,  1,  0, -1,  3,  0},
            {-1,  0,  3,  1,  0,  2,  4,  0, -1,  1},
            { 2,  0, -1,  3,  0,  1,  0,  4,  2,  0},
            { 0,  1,  0, -1,  2,  0,  3,  0,  5, -1}
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

        int[][] layout = layoutByLevel(currentLevel); // lấy layout theo level
        this.row = layout.length;
        this.col = layout[0].length;

        this.brickWidth = (totalWidth - (col - 1) * spacing) / col;
        this.brickHeight = (totalHeight - (row - 1) * spacing) / row;

        bricks = new Brick[row][col];
        createBricks(layout, startX, startY);
    }

    // Lấy layout dựa trên level hiện tại
    private int[][] layoutByLevel(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> LEVEL1;
            case 2 -> LEVEL2;
            case 3 -> LEVEL3;
            default -> LEVEL1;
        };
    }

    // Tạo các viên gạch dựa trên layout
    private void createBricks(int[][] layout, double startX, double startY) {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int value = layout[r][c];

                double x = startX + c * (brickWidth + spacing);
                double y = startY + r * (brickHeight + spacing);

                if (value != 0) { // nếu không phải ô trống
                    Brick brick;

                    if (value == -1) {
                        brick = new UnbreakableBrick(x, y, brickWidth, brickHeight);
                    } else {
                        brick = new NormalBrick(x, y, brickWidth, brickHeight, value);
                    }

                    bricks[r][c] = brick;
                } else {
                    bricks[r][c] = null;
                }
            }
        }
    }

    // Chuyển sang level tiếp theo
    public Level nextLevel(double startX, double startY, double totalWidth, double totalHeight) {
        currentLevel++;
        if (currentLevel > MAX_LEVEL_BRICK) {
            currentLevel = 1;
        }
        return new Level(currentLevel, startX, startY, totalWidth, totalHeight);
    }

    // Render các viên gạch
    public void render(GraphicsContext gc) {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !brick.isDestroyed()) { // chỉ render gạch chưa bị phá
                    brick.render(gc);
                }
            }
        }
    }

    // Kiểm tra xem tất cả gạch thường đã bị phá chưa
    public boolean isCleared() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !"unbreakable".equals(brick.getType()) && !brick.isDestroyed()) {
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

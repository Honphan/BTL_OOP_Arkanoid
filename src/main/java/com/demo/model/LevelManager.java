package com.demo.model;

import com.demo.model.brick.*;
import javafx.scene.canvas.GraphicsContext;

public class LevelManager {
    // Mảng lưu trữ các viên gạch của level
    private Brick[][] bricks;
    private int currentLevel;
    private int row;
    private int col;

    private static final double brickWidth = 50;
    private static final double brickHeight = 25;

    public static final int MAX_LEVEL_BRICK = 4; // số level tối đa

    // Layout các level, -1 = unbreakable, 0 = trống, >0 = hit points của gạch
    public static final int[][] LEVEL2 = {
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2},
            {2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
    };

    public static final int[][] LEVEL3 = {
            {1, 1, 4, 2, 2, 5, 3, 3, 6, -2},
            {1, 4, 2, 2, 5, 3, 3, 6, -2, 6},
            {4, 2, 2, 5, 3, 3, 6, -2, 6, 3},
            {2, 2, 5, 3, 3, 6, -2, 6, 3, 3},
            {2, 5, 3, 3, 6, -2, 6, 3, 3, 5},
            {5, 3, 3, 6, -2, 6, 3, 3, 5, 2},
            {3, 3, 6, -2, 6, 3, 3, 5, 2, 2},
            {3, 6, -2, 6, 3, 3, 5, 2, 2, 4},
            {6, -2, 6, 3, 3, 5, 2, 2, 4, 1},
            {-2, 6, 3, 3, 5, 2, 2, 4, 1, 1},
            {-999, -999, -999, -999, -999, -999, -999, -999, -999, -999}
    };

    public static final int[][] LEVEL4 = {
            {-999, -999, -999, -999, -999, -999, -999, -999, -999, -999},
            {-999, -1, -1, -1, 2, 2, -1, -1, -1, -999},
            {-999, -1, -1, -1, -1, -1, -1, -1, -1, -999},
            {-999, -1, -1, -1, 1, 1, -1, -1, -1, -999},
            {-999, 2, -1, 1, 3, 3, 1, -1, 2, -999},
            {-999, 2, -1, 1, 3, 3, 1, -1, 2, -999},
            {-999, -1, -1, -1, 1, 1, -1, -1, -1, -999},
            {-999, -1, -1, -1, -1, -1, -1, -1, -1, -999},
            {-999, -1, -1, -1, 2, 2, -1, -1, -1, -999}
    };

    public static final int[][] LEVEL5 = {

            {-999, -999, -999, -999, -999, -999, -999, -999, -999},
            {3, 0, 0, 0, 0, 0, 0, 0, 0},
            {3, 3, 0, 0, 0, 0, 0, 0, 0},
            {3, 3, 3, 0, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 3, 0, 0, 0},
            {3, 3, 3, 3, 3, 3, 3, 0, 0},
            {3, 3, 3, 3, 3, 3, 3, 3, 0},
            {6, 6, 6, 6, 6, 6, 6, 6, 6},
            {-999, -999, -999, -999, -999, -999, -999, -999, -999}
    };

    public static final int[][] LEVEL6 = {
            {0,0,0,0,0,4,0,0,0,0,0},
            {0,0,1,1,1,4,1,1,1,0,0},
            {0,1,1,1,-999,-999,-999,1,1,1,0},
            {0,1,1,1,-999,-999,-999,1,1,1,0},
            {1,1,1,-999,-999,-999,-999,-999,1,1,1},
            {1,1,1,-999,-999,-999,-999,-999,1,1,1},
            {1,1,1,-999,-999,-999,-999,-999,1,1,1},
            {1,1,1,-999,-999,-999,-999,-999,1,1,1},
            {4,0,0,4,0,4,0,4,0,0,4},
            {0,0,0,0,0,4,0,0,0,0,0},
            {0,0,0,0,0,4,0,0,0,0,0},
            {0,0,0,0,0,4,0,0,0,0,0},
            {0,0,0,0,0,4,0,0,0,0,0},
            {0,0,0,0,3,6,3,0,0,0,0},
            {0,0,0,0,0,6,0,0,0,0,0},
            {0,0,0,0,0,6,0,0,0,0,0}

    };

    public static final int[][] LEVEL7 = {
            {0,0,3,0,0,0,0,0,3,0,0},
            {0,0,3,0,0,0,0,0,3,0,0},
            {0,0,0,3,0,0,0,3,0,0,0},
            {0,0,0,4,4,4,4,4,0,0,0},
            {0,0,4,4,4,4,4,4,4,0,0},
            {0,4,4,-2,4,4,4,-2,4,4,0},
            {4,4,4,-2,4,4,4,-2,4,4,4},
            {4,4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4,4},
            {4,0,4,0,0,0,0,0,4,0,4},
            {0,0,4,4,4,0,4,4,4,0,0}
    };

    public static final int[][] LEVEL1 = {
            {-999,-999,-999,-999,-999,-999,-999,-999,-999,-999,-999,-999,-999},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1},
            {0,0,-1,4,-1,0,-1,4,-1,0,-1,4,-1},
            {0,0,-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1,0},
            {0,-1,5,-1,0,-1,5,-1,0,-1,5,-1,0},
            {0,-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0},
            {-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1,0,0},
            {-1,6,-1,0,-1,6,-1,0,-1,6,-1,0,0},
            {-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1,0,0}

    };



    public LevelManager(int currentLevel,double totalWidth, double totalHeight) {
        this.currentLevel = currentLevel;

        int[][] layout = layoutByLevel(currentLevel); // lấy layout theo level
        this.row = layout.length;
        this.col = layout[0].length;

        double startX = (totalWidth - col * brickWidth) / 2;
        double startY = 40;

        bricks = new Brick[row][col];
        createBricks(layout, startX, startY);
    }

    // Lấy layout dựa trên level hiện tại
    private int[][] layoutByLevel(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> LEVEL1;
            case 2 -> LEVEL2;
            case 3 -> LEVEL3;
            case 4 -> LEVEL4;
            default -> LEVEL1;
        };
    }

    // Tạo các viên gạch dựa trên layout
    private void createBricks(int[][] layout, double startX, double startY) {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int value = layout[r][c];

                double x = startX + c * brickWidth;
                double y = startY + r * brickHeight;

                if (value != 0) { // nếu không phải ô trống
                    Brick brick = null;

                    if (value == -999) {
                        brick = new UnbreakableBrick(x, y, brickWidth, brickHeight);
                    }
                    if (value == -1) {
                        brick = new HiddenBrick(x, y, brickWidth, brickHeight);
                    }
                    if (value == -2) {
                        brick = new BombBrick(x, y, brickWidth, brickHeight, bricks, r, c);
                    }
                    if (value <= 9 && value >= 7) {
                        brick = new PowerUpBrick(x, y, brickWidth, brickHeight);
                    }
                    if (value <= 3 && value >= 1) {
                        brick = new NormalBrick(x, y, brickWidth, brickHeight, value);
                    }
                    if (value <= 6 && value >= 4) {
                        brick = new StrongBrick(x, y, brickWidth, brickHeight, value);
                    }
                    if (brick != null) {
                        bricks[r][c] = brick;
                    }
                } else {
                    bricks[r][c] = null;
                }
            }
        }
    }

    // Chuyển sang level tiếp theo
    public LevelManager nextLevel(double startX, double startY, double totalWidth, double totalHeight) {
        currentLevel++;
        if (currentLevel > MAX_LEVEL_BRICK) {
            currentLevel = 1;
        }
        return new LevelManager(currentLevel, totalWidth, totalHeight);
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
    public void update() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Brick brick = bricks[r][c];
                if (brick != null && !brick.isDestroyed()) {
                    brick.update();
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

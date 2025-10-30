package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Gạch mạnh: chịu được 2 hit, đổi ảnh khi nứt
public class StrongBrick extends Brick {

    private static final int MAX_HP = 2;
    private Image[] imageNormal;
    private Image[] imageCracked;
    private int colorStyle;

    public StrongBrick(double x, double y, double width, double height, int colorStyle) {
        super(x, y, width, height, MAX_HP, "strong");
        this.colorStyle = colorStyle;

        imageNormal = new Image[4];
        imageCracked = new Image[4];

        try {
            imageNormal[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/blue-normal.png"));
            imageCracked[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/blue-cracked.png"));

            imageNormal[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/violet-normal.png"));
            imageCracked[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/violet-cracked.png"));

            imageNormal[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/orange-normal.png"));
            imageCracked[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/orange-cracked.png"));
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch cứng: " + e.getMessage());;
        }
    }

    @Override
    public void takeHit() {
        if (!isDestroyed()) {
            setHitPoints(getHitPoints() - 1);
            if (getHitPoints() <= 0) {
                setDestroyed(true);
            }
        }
    }

    @Override
    public void update() {
        // StrongBrick không có hành vi đặc biệt, chỉ đổi ảnh theo HP
    }

    @Override
    public int getScoreValue() {
        return 25;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return;

        Image img = null;
        switch (colorStyle) {
            case 6 -> {
                if (getHitPoints() == MAX_HP) {
                    img = imageNormal[3];
                } else {
                    img = imageCracked[3];
                }
            }
            case 5 -> {
                if (getHitPoints() == MAX_HP) {
                    img = imageNormal[2];
                }  else {
                    img = imageCracked[2];
                }
            }
            default -> {
                if (getHitPoints() == MAX_HP) {
                    img = imageNormal[1];
                }
                else {
                    img = imageCracked[1];
                }
            }

        }

        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }

    public int getColorStyle() {
        return colorStyle;
    }

    public void setColorStyle(int colorStyle) {
        this.colorStyle = colorStyle;
    }
}

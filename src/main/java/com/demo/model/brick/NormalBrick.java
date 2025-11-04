package com.demo.model.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Gạch bình thường: giảm máu khi trúng bóng, có thể bị phá hủy
public class NormalBrick extends Brick {

    private Image[] image;
    private int colorStyle;
    private static final int MAX_HP = 1;

    public NormalBrick(double x, double y, double width, double height, int colorStyle) {
        super(x, y, width, height, MAX_HP, "normal");
        this.colorStyle = colorStyle;

        image = new Image[4];

        try {

            image[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/lightblue-normal.png"));
            image[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/yellow-normal.png"));
            image[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/green-normal.png"));

        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch thường: " + e.getMessage());
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
        // NormalBrick không có hành vi đặc biệt, chỉ cần check destroyed trong game loop
    }


    public int getScoreValue() {
        return 10; // NormalBrick mang 10 điểm
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return;
        // Gán hình ảnh cho các case hitPoints
        Image img = switch (colorStyle) {
            case 3 -> image[3];
            case 2 -> image[2];
            default -> image[1];
        };

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

    public NormalBrick(double x, double y, double width, double height, int colorStyle, boolean loadImage) {
        super(x, y, width, height, MAX_HP, "normal");
        this.colorStyle = colorStyle;

        if (loadImage) {
            image = new Image[4];
            try {
                image[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/blue-normal.png"));
                image[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/green-normal.png"));
                image[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/yellow-normal.png"));
            } catch (Exception e) {
                System.err.println("Không load được ảnh gạch thường: " + e.getMessage());
            }
        } else {
            image = new Image[4]; // tránh NPE
        }
    }

    public Image[] getImage() {
        return image;
    }

    public void setImage(Image[] image) {
        this.image = image;
    }
}
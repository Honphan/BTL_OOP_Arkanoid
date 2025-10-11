package com.demo.model.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class NormalBrick extends Brick{

    private Image[] brickImage;

    public NormalBrick(double x, double y, double width, double height, int hitPoints) {
        super(x, y, width, height, hitPoints, "normal");

        brickImage = new Image[6];

        try {

            brickImage[1] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/1hp_green1.png"));
            brickImage[2] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/2hp_violet1.png"));
            brickImage[3] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/3hp_blue1.png"));
            brickImage[4] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/4hp_yellow1.png"));
            brickImage[5] = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/5hp_red1.png"));

        } catch (Exception e) {
            System.out.println("Không thể load ảnh cho NormalBrick: " + e.getMessage());
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) return;

        Image imgToDraw = switch (getHitPoints()) {
            case 5 -> brickImage[5];
            case 4 -> brickImage[4];
            case 3 -> brickImage[3];
            case 2 -> brickImage[2];
            default -> brickImage[1];
        };

        if (imgToDraw != null)
            gc.drawImage(imgToDraw, getX(), getY(), getWidth(), getHeight());
        else {
            gc.setFill(javafx.scene.paint.Color.GRAY);
            gc.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void takeHit() {
        if (isDestroyed()) return;

        setHitPoints(getHitPoints() - 1);
        if (getHitPoints() <= 0) {
            setDestroyed(true);
        }
    }


}

package com.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends GameObject{
    private int hitPoints;
    private String type;
    private boolean destroyed;

    public Brick(int x, int y, int width, int height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
        this.destroyed = false;
        node = new Rectangle(width, height);
        node.setLayoutX(x);
        node.setLayoutY(y);
    }


    public void takeHit() {
        if (!destroyed && !"unbreakable".equals(type)) {
            hitPoints--;
            if (hitPoints <= 0) destroyed = true;
        }
    }

    public void updateColor() {
        Color color;
        if ("unbreakable".equals(type)){
           color = Color.GREY;
        } else {
            color = switch (hitPoints) {
                case 3 -> Color.RED;
                case 2 -> Color.GREEN;
                case 1 -> Color.YELLOW;
                default -> Color.BLACK;
            };
        }
        node.setFill(color);
    }

    public void destroy() {
        node.setVisible(false);
    }
    public boolean isDestroyed(){
        return destroyed;
    }

    public int getHitPoints(){
        return hitPoints;
    }

    public String getType(){
        return type;
    }

}

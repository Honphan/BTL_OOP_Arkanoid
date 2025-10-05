package com.demo.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GameObject{
    private int hitPoints;
    private int type;

    public Brick(int x, int y, int width, int height, int hitPoints, int type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
        switch (type) {
            case 1 -> node.setFill(Color.RED);
            case 4 -> node.setFill(Color.BLUE);
            case 2 -> node.setFill(Color.GREEN);
            case 3 -> node.setFill(Color.YELLOW);
            default -> node.setFill(Color.GRAY);

        }
    }


    public void takeHit(){
        if (hitPoints > 0){
            hitPoints--;
            updateColor();
            if (hitPoints == 0){
                destroy();
            }
        }
    }

    public void destroy(){
        node.setVisible(false);
    }
    public void updateColor(){
        Color currentColor = (Color) node.getFill();
        node.setFill(currentColor.darker());
    }

    public boolean isDestroyed(){
        return hitPoints <= 0;
    }

    public int getHitPoints(){
        return hitPoints;
    }

    public int getType(){
        return type;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(GraphicsContext gc) {

    }

}

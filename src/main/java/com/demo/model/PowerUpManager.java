package com.demo.model;

import com.demo.model.powerup.*;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Random;


public class PowerUpManager {

    private ArrayList<PowerUp> powerups;
    private Random random;

    public PowerUpManager () {
        this.powerups = new ArrayList<>();
        this.random = new Random();
    }

    public void spawnPowerUp(double x, double y) {
        int event = random.nextInt(100);

        if (event > 15) return;

        int type = random.nextInt(4); // 4 loại power-up

        PowerUp powerup = switch (type) {
            case 0 -> new ExpandPaddlePowerUp(x, y, 32, 16, 2.5);
            case 1 -> new ExtraLifePowerUp(x, y, 32, 16, 2.5);
            case 2 -> new PiercingBallPowerUp(x, y, 32, 16, 2.5);
            case 3 -> new MultiBallPowerUp(x, y, 32, 16, 2.5);
            default -> null;
        };

        if (powerup != null) powerups.add(powerup);
    }


    public void render(GraphicsContext gc) {
        for (PowerUp p : powerups) {
            p.render(gc);
        }
    }

    public ArrayList<PowerUp> getPowerUps() {
        return powerups;
    }

}

package com.demo.model;

import com.demo.model.powerup.*;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class PowerUpManager {

    private ArrayList<PowerUp> powerups;

    public PowerUpManager () {
        this.powerups = new ArrayList<>();
    }

    public void addPowerUp(PowerUp powerup) {
        powerups.add(powerup);
    }

    public void update(double canvasHeight) {
        Iterator<PowerUp> iterator = powerups.iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();
            p.update(); // di chuyển powerup xuống

            // Nếu rơi ra ngoài màn hình
            if (p.getY() > canvasHeight) {
                iterator.remove();
            }
        }
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

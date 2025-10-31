package com.demo.model.brick;

import com.demo.model.powerup.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;

public class PowerUpBrick extends Brick {

    private PowerUpType spawnType;
    private Image image;
    private static final int MAX_HP = 1;

    protected static final int defaultVelocityY = 3;

    public PowerUpBrick(double x, double y, double width, double height) {
        super(x, y, width, height, MAX_HP, "powerup");

        try {
            image = new Image(getClass().getResourceAsStream("/com/demo/view/image/bricks/yellow-normal.png"));
        } catch (Exception e) {
            System.err.println("Không load được ảnh gạch power-up: " + e.getMessage());
        }

        Random random = new Random();

        if (random.nextInt(100) < 100) {
            int choice = random.nextInt(7);
            spawnType = switch (choice) {
                case 0 -> PowerUpType.EXPAND_PADDLE;
                case 1 -> PowerUpType.EXTRA_LIFE;
                case 2 -> PowerUpType.PIERCING_BALL;
                case 3 -> PowerUpType.MULTI_BALL;
                case 4 -> PowerUpType.SHRINK_PADDLE;
                case 5 -> PowerUpType.SPEED_UP;
                case 6 -> PowerUpType.SPEED_DOWN;
                default -> null;
            };

        } else {
            spawnType = null;
        }
    }

    /** Khi gạch bị phá, gọi hàm này để sinh PowerUp */
    public PowerUp spawnPowerUp() {
        if (spawnType == null) return null;

        double powerupWidth = 32;
        double powerupHeight = 16;
        double spawnX = getX() + (getWidth() - powerupWidth) / 2;
        double spawnY = getY() + getHeight();

        return switch (spawnType) {
            case EXPAND_PADDLE -> new ExpandPaddlePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case EXTRA_LIFE -> new ExtraLifePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case PIERCING_BALL -> new PiercingBallPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case MULTI_BALL -> new MultiBallPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SHRINK_PADDLE -> new ShrinkPaddlePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SPEED_DOWN ->  new SpeedDownPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SPEED_UP -> new SpeedUpPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            default -> null;
        };
    }

    @Override
    public void takeHit() {
        setDestroyed(true);
    }

    @Override
    public void update() {}

    @Override
    public void render(GraphicsContext gc) {
        if (image != null && !isDestroyed()) {
            gc.drawImage(image, x, y, width, height);
        }
    }

    public int getScoreValue() {return 10;}
}

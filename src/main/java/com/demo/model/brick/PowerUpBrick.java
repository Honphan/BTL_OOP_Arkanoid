package com.demo.model.brick;

import com.demo.model.powerup.*;
import javafx.scene.canvas.GraphicsContext;

/**
 * Gạch đặc biệt có khả năng sinh PowerUp khi bị phá.
 * Tái sử dụng hành vi và hình ảnh từ gạch gốc thông qua composition.
 */
public class PowerUpBrick extends Brick {

    private final Brick baseBrick; // Gạch gốc (Normal, Strong, Hidden, Bomb...)
    private final PowerUpType spawnType;
    private boolean powerUpSpawned = false;
    private static final int defaultVelocityY = 3;

    private boolean disableSpawn = false;

    public PowerUpBrick(Brick baseBrick, PowerUpType spawnType) {
        super(baseBrick.getX(), baseBrick.getY(), baseBrick.getWidth(), baseBrick.getHeight(), baseBrick.getHitPoints(), "powerup");
        this.baseBrick = baseBrick;
        this.spawnType = spawnType;
    }

    @Override
    public void update() {
        baseBrick.update();
    }

    @Override
    public void render(GraphicsContext gc) {
        baseBrick.render(gc);
    }

    @Override
    public void takeHit() {
        baseBrick.takeHit();
        if (baseBrick.isDestroyed()) {
            this.setDestroyed(true);
        }
    }

    @Override
    public boolean isDestroyed() {
        return baseBrick.isDestroyed();
    }

    @Override
    public boolean isDestructible() {
        return baseBrick.isDestructible();
    }

    @Override
    public int getHitPoints() {
        return baseBrick.getHitPoints();
    }

    @Override
    public int getScoreValue() {
        return baseBrick.getScoreValue();
    }

    /**
     * Sinh PowerUp nếu chưa từng sinh và gạch đã bị phá.
     */
    public PowerUp spawnPowerUp() {
        if (powerUpSpawned || spawnType == null || !isDestroyed() || disableSpawn) return null;
        powerUpSpawned = true;

        double powerupWidth = 40;
        double powerupHeight = 20;
        double spawnX = getX() + (getWidth() - powerupWidth) / 2;
        double spawnY = getY() + getHeight();

        return switch (spawnType) {
            case EXPAND_PADDLE -> new ExpandPaddlePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case EXTRA_LIFE -> new ExtraLifePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case PIERCING_BALL -> new PiercingBallPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case MULTI_BALL -> new MultiBallPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SHRINK_PADDLE -> new ShrinkPaddlePowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SPEED_DOWN -> new SpeedDownPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case SPEED_UP -> new SpeedUpPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
            case LASER -> new LaserPowerUp(spawnX, spawnY, powerupWidth, powerupHeight, defaultVelocityY);
        };
    }

    public void setDisableSpawn(boolean disable) {
        this.disableSpawn = disable;
    }

    public Brick getBaseBrick() {
        return baseBrick;
    }

    public PowerUpType getSpawnType() {
        return spawnType;
    }

    public boolean isPowerUpSpawned() {
        return powerUpSpawned;
    }

    public void setPowerUpSpawned(boolean powerUpSpawned) {
        this.powerUpSpawned = powerUpSpawned;
    }

    public boolean isDisableSpawn() {
        return disableSpawn;
    }
}

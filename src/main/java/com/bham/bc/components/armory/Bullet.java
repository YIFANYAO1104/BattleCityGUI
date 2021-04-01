package com.bham.bc.components.armory;

import com.bham.bc.components.characters.Side;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import javafx.scene.image.Image;

/**
 * Represents any bullet and defines common bullet properties
 * <p> TODO: add constrains / asserts / throw exceptions in setters
 */
abstract public class Bullet extends MovingEntity {
    private final BulletType TYPE;
    private Side side;
    private double damage;

    /**
     * <p>Constructs a bullet using with geometrical properties to determine the position of a bullet on each frame.
     * SIDE property is used to indicate which team the bullet can damage. </p>
     *
     * @param x      top left position in x axis
     * @param y      top left position in y axis
     * @param speed  velocity value at which the bullet will move
     * @param angle  angle at which the bullet will move
     * @param type   bullet type (e.g., DEFAULT, EXPLOSIVE)
     * @param side   ALLY or ENEMY side the bullet belongs to
     * @param damage amount of hp the bullet can take from an entity
     */
    public Bullet(double x, double y, double speed, double angle, BulletType type, Side side, double damage) {
        super(x, y, speed);
        this.angle = angle;
        this.TYPE = type;
        this.side = side;
        this.damage = damage;

        entityImages = new Image[] { TYPE.getImage() };
    }

    /**
     * Gets bullet's speed
     * @return velocity at which the bullet is moving
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Gets bullet's type
     * @return DEFAULT of EXPLOSIVE type the bullet belongs to
     * TODO: add more types
     */
    public BulletType getType() {
        return TYPE;
    }

    /**
     * Gets bullet's side
     * @return ALLY or ENEMY side the bullet belongs to
     */
    public Side getSide() {
        return side;
    }

    /**
     * Gets bullet's damage
     * @return amount of damage the bullet deals
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets bullet's speed
     * <br><b>Note:</b> the speed must be in range [1, 20]
     * <br>TODO: assert range
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets bullet's damage
     *
     * <br><b>Note:</b> the damage must be in range [-500, 500]
     * <br>TODO: assert range
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Unregisters and prepares to remove the bullet. Also runs any destruction effects
     */
    public abstract void destroy();

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Bullet"; }
}
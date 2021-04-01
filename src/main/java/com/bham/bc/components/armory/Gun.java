package com.bham.bc.components.armory;

import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.time.CrudeTimer.CLOCK;

/**
 * This class controls the way the bullet can be fired with respect to the game timer. It must be bind with a character.
 */
public class Gun {
    private final GameCharacter CHARACTER;
    private BulletType bulletType;
    private long lastTick;
    private long rate;

    /**
     * A default constructor to create gun with no bullets
     *
     * @param character {@link GameCharacter} the gun belongs to
     */
    public Gun(GameCharacter character) {
        this.CHARACTER = character;
        this.bulletType = null;
        this.lastTick = CLOCK.getCurrentTime();
        this.rate = 0;
    }

    /**
     * Alternate constructor to create a gun with the specified bullet type
     *
     * @param character  {@link GameCharacter} the gun belongs to
     * @param bulletType type of bullet the gun should control
     */
    public Gun(GameCharacter character, BulletType bulletType) {
        this.CHARACTER = character;
        this.bulletType = bulletType;
        this.lastTick = CLOCK.getCurrentTime();
        this.rate = bulletType.getMinRate();
    }

    /**
     * Updates the bullet type this gun is shooting
     * @param bulletType the type of bullet this gun will handle
     */
    public void setBulletType(BulletType bulletType) {
        this.bulletType = bulletType;

    }

    /**
     * Sets this weapon's rate assuring it is not faster than the minimum rate
     * @param rate the milliseconds this gun should wait before allowing to shoot the next bullet
     */
    public void setRate(long rate) {
        if(bulletType != null) this.rate = Math.min(bulletType.getMinRate(), rate);
    }

    /**
     * <p>Creates a new bullet object that depends on this class <b>bulletType</b> parameter. If angle offset is set to 0,
     * this method creates a bullet straightly in front of the character just touching its "nose". Otherwise, the bullet is
     * rotated with respect to <i>its</i> center.</p>
     *
     * <p><b>Note:</b> the bullet is always spawned aligned with the center of the character's width</p>
     *
     * @param angleOffset offset by which the bullet should be rotated around its center
     * @return the created {@link Bullet} object with respect to the character's location
     */
    private Bullet spawnBullet(double angleOffset) {
        double positionOffset = 0;
        // Define the bullet's center coordinates
        double centerBulletX = CHARACTER.getCenterPosition().getX();
        double centerBulletY = CHARACTER.getPosition().getY() - bulletType.getHeight() / 2.0;

        // Rotate bullet's center point around character
        Rotate rotateAroundCharacter = new Rotate(CHARACTER.getAngle(), CHARACTER.getCenterPosition().getX(), CHARACTER.getCenterPosition().getY());
        Point2D transformedCenterXY = rotateAroundCharacter.transform(centerBulletX, centerBulletY);

        // Define the bullet's top left coordinates
        double topLeftBulletX = transformedCenterXY.getX() - bulletType.getWidth() / 2.0;
        double topLeftBulletY = transformedCenterXY.getY() - bulletType.getHeight() / 2.0;

        // Return an instance which corresponds to the bullet type
        switch(bulletType) {
            case EXPLOSIVE:
                return new ExplosiveBullet(topLeftBulletX, topLeftBulletY, CHARACTER.getAngle() + angleOffset, CHARACTER.getSide());
            default:
                return new DefaultBullet(topLeftBulletX, topLeftBulletY, CHARACTER.getAngle() + angleOffset, CHARACTER.getSide());
        }
    }

    /**
     * Shoots exactly 1 bullet in front of the character
     */
    public void shoot() {
        if(bulletType == null || CLOCK.getCurrentTime() - lastTick < rate) return;

        Bullet bullet = spawnBullet(0);
        backendServices.addBullet(bullet);
        lastTick = CLOCK.getCurrentTime();
    }

    /**
     * Shoots multiple bullets at the specified angles. The center spawn point is the same for all bullets.
     * @param angles any number or angles for each of which the bullet will be fired
     */
    public void shoot(double ...angles) {
        if(bulletType == null || CLOCK.getCurrentTime() - lastTick < rate) return;

        List<Bullet> bullets = Arrays.stream(angles).mapToObj(this::spawnBullet).collect(Collectors.toList());
        bullets.forEach(bullet -> backendServices.addBullet(bullet));
        lastTick = CLOCK.getCurrentTime();
    }

}

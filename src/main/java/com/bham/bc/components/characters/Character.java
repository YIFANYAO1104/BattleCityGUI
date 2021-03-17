package com.bham.bc.components.characters;

import static com.bham.bc.components.CenterController.backendServices;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.entity.DIRECTION;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class Character extends MovingEntity {

    protected int hp;
    protected SIDE side;
    protected EnumSet<DIRECTION> directionSet;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     * @param speed value which defines the initial velocity
     */
    protected Character(double x, double y, double speed, int hp, SIDE side) {
        super(x, y, speed);
        this.hp = hp;
        this.side = side;
        directionSet = EnumSet.noneOf(DIRECTION.class);
    }

    /**
     * Updates angle at which the player is facing
     *
     * <p>This method goes through every direction in the directionSet, coverts them to basis vectors,
     * adds them up to get a final direction vector and calculates the angle between it and (0, 1)</p>
     *
     * <b>Note:</b> the basis vector which is used for angle calculation must be (0, 1) as this is the
     * way the character in the image is facing (upwards)
     */
    protected void updateAngle() {
        Optional<Point2D> directionPoint = directionSet.stream().map(DIRECTION::toPoint).reduce(Point2D::add);
        directionPoint.ifPresent(p -> { if(p.getX() != 0 || p.getY() != 0) angle = p.angle(0, 1) * (p.getX() > 0 ? 1 : -1); });
    }

    public void switchWeapon(Weapon w) {}
    public abstract void increaseHP(int health);


    /**
     * Handles bullet collision
     * @param bullet
     */
    protected void handleBullet(Bullet bullet) {
        if(intersects(bullet)) {
            if(bullet.side != side) {
                hp -= bullet.getDamage();
            }
            bullet.destroy();
        }
    }

    /**
     * Handles character collision
     * @param character
     */
    protected void handleCharacter(Character character) {
        if(this != character && intersects(character)) {
            move(-1, true);
        }
    }

    public void handleAll(List<Character> characters, List<Bullet> bullets) {
        characters.forEach(this::handleCharacter);
        bullets.forEach(this::handleBullet);
    }


    /**
     * Overloads basic <i>move()</i> method with extra parameters
     *
     * @param speedMultiplier number by which the speed will be multiplied (use negative to inverse movement)
     * @param force boolean indicating if the character should move even if the directionSet is empty
     */
    public void move(double speedMultiplier, boolean force) {
        double deltaX = Math.sin(Math.toRadians(angle)) * speed;
        double deltaY = Math.cos(Math.toRadians(angle)) * speed;

        if(force) {
            x += deltaX * speedMultiplier;
            y -= deltaY * speedMultiplier;
        } else if(!directionSet.isEmpty()) {
            x += deltaX * speedMultiplier;
            y -= deltaY * speedMultiplier;
        }
    }

    @Override
    public void move() {
        if(!directionSet.isEmpty()) {
            x += Math.sin(Math.toRadians(angle)) * speed;
            y -= Math.cos(Math.toRadians(angle)) * speed;
        }
    }
}

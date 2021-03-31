package com.bham.bc.components.characters;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class GameCharacter extends MovingEntity {
    private final double MAX_HP;
    protected double hp;
    protected SIDE side;

    protected Steering sb;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     * @param speed value which defines the initial velocity
     */
    protected GameCharacter(double x, double y, double speed, double hp, SIDE side) {
        super(x, y, speed);
        MAX_HP = hp;
        this.hp = hp;
        this.side = side;
        sb = new Steering(this);
    }

    /**
     * Gets the HP of the player
     * @return current HP
     */
    public double getHp() { return hp; }

    /**
     * Gets character's side
     * @return ALLY or ENEMY side the character belongs to
     */
    public SIDE getSide() { return side; }

    /**
     * Increases or decreases HP for the player
     * @param health amount by which the player's HP is changed
     */
    public void changeHP(double health) {
        hp = Math.min(hp + health, MAX_HP);
        if(hp <= 0) destroy();
    }

    @Deprecated
    public void switchWeapon(Weapon w) {}


    /**
     * Handles bullet collision - takes damage and destroys bullet
     * @param bullet bullet to handle
     */
    protected void handleBullet(Bullet bullet) {
        if(intersects(bullet)) {
            if(bullet.getSide() != side) {
                changeHP(-bullet.getDamage());
            }
            bullet.destroy();
        }
    }

    /**
     * Handles character collision - moves back
     * @param gameCharacter character to handle
     */
    protected void handleCharacter(GameCharacter gameCharacter) {
        if(this.getID() != gameCharacter.getID() && intersects(gameCharacter)) {
            move(-1);
        }
    }

    /**
     * Handles a list of characters and bullets
     * @param gameCharacters list of characters to handle
     * @param bullets list of bullets to handle
     */
    public void handleAll(List<GameCharacter> gameCharacters, List<Bullet> bullets) {
        gameCharacters.forEach(this::handleCharacter);
        bullets.forEach(this::handleBullet);
    }


    /**
     * Overloads basic <i>move()</i> method with extra speed multiplier parameter
     * <br>TODO: assure speedMultiplier is within [-5, 5]
     * @param speedMultiplier number by which the speed will be multiplied (use negative to inverse movement)
     */
    public void move(double speedMultiplier) {
        //TODO:Move to steering.calculate
//        velocity = new Point2D(Math.sin(Math.toRadians(angle)),Math.cos(Math.toRadians(angle))).multiply(speed).multiply(speedMultiplier);
        Point2D temp = velocity.multiply(speedMultiplier);
        velocity = velocity.multiply(speedMultiplier);
        x += temp.getX();
        y += temp.getY();
    }

    @Override
    public void move() {
        //TODO:Move to steering.calculate
//        velocity = new Point2D(Math.sin(Math.toRadians(angle)),Math.cos(Math.toRadians(angle))).multiply(speed);

        x += velocity.getX();
        y += velocity.getY();
    }

    protected abstract void destroy();

    /**
     *For path smoothing debug
     */
    abstract public List<Shape> getSmoothingBoxes();


}

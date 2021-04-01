package com.bham.bc.components.characters;

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
abstract public class GameCharacter extends MovingEntity {
    private final double MAX_HP;
    protected double hp;
    protected SIDE side;
    protected EnumSet<DIRECTION> directionSet;
    protected boolean TRAPPED;

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
        directionSet = EnumSet.noneOf(DIRECTION.class);
    }

    /**
     * Updates angle at which the player is facing
     *
     * <p>This method goes through every direction in the directionSet, coverts them to basis vectors,
     * adds them up to get a final direction vector and calculates the angle between it and (0, 1)</p>
     *
     * <p><b>Note:</b> the basis vector which is used for angle calculation must be (0, 1) as this is the
     * way the character in the image is facing (upwards)</p>
     */
    protected void updateAngle() {
        Optional<Point2D> directionPoint = directionSet.stream().map(DIRECTION::toPoint).reduce(Point2D::add);
        directionPoint.ifPresent(p -> { if(p.getX() != 0 || p.getY() != 0) angle = p.angle(0, 1) * (p.getX() > 0 ? 1 : -1); });
    }

    /**
     * Gets the HP of the player
     * @return current HP
     */
    public double getHp() { return hp; }

    /**
     * Increases or decreases HP for the player
     * @param health amount by which the player's HP is changed
     */
    public void addHP(double health) {
        hp = Math.min(hp + health, MAX_HP);
        if(hp <= 0) exists = false;
    }

    /**
     * Using speedBuff to change current speed
     * @param speed
     */
    public void speedUp(double speed){
        this.speed = speed;
    }

    /**
     * Using armorBUff to reset entity's HP TO A NEW HIGHER LEVEL
     * @param HP
     */
    public void armorUP(int HP){
        this.hp = HP;
    }
    @Deprecated
    public void switchWeapon(Weapon w) {}

    public void setTRAPPED(){
        this.TRAPPED=true;
    }
    public void setUNTRAPPED(){
        this.TRAPPED=false;
    }

    /**
     * Handles bullet collision - takes damage and destroys bullet
     * @param bullet bullet to handle
     */
    protected void handleBullet(Bullet bullet) {
        if(intersects(bullet)) {
            if(bullet.getSide() != side) {
                addHP(-bullet.getDamage());
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
            move(-1, true);
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
     * Overloads basic <i>move()</i> method with extra parameters
     * <br>TODO: assure speedMultiplier is within [-5, 5]
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
    public void teleport(double x,double y){
        this.x = x;
        this.y =y;
    }
    public void destroyed(){
        this.hp-=200;
    }
}

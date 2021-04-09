package com.bham.bc.components.characters;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.triggers.powerups.Weapon;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

import static com.bham.bc.utils.GeometryEnhanced.isZero;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class GameCharacter extends MovingEntity {
    public static final int MAX_SIZE = 32;

    // Private properties
    private final double MAX_HP;
    protected double hp;
    protected Side side;

    // Mechanics / movement
    protected double mass;
    protected Steering steering;
    protected Point2D acceleration;

    // Triggers
    protected int immuneTicks, freezeTicks, tripleTicks = 0;
    protected boolean TRAPPED;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     * @param speed value which defines the initial velocity
     */
    protected GameCharacter(double x, double y, double speed, double hp, Side side) {
        super(x, y, speed);
        MAX_HP = hp;
        this.hp = hp;
        this.side = side;

        mass = 3;
        steering = new Steering(this);
        acceleration = new Point2D(0,0);
    }

    /**
     * Gets character's side
     * @return ALLY or ENEMY side the character belongs to
     */
    public Side getSide() { return side; }

    /**
     * Gets the HP of the player
     * @return current HP
     */
    public double getHp() { return hp; }

    /**
     * Increases or decreases HP for the player
     * @param health amount by which the player's HP is changed
     */
    public void changeHp(double health) {
        hp = Math.min(hp + health, MAX_HP);
        if(side == Side.ENEMY) System.out.println("Enemy's HP: " + hp);
        if(hp <= 0) destroy();
    }

    // TEMP: DOCUMENT ------------------------------------------------
    @Deprecated
    public void switchWeapon(Weapon w) {}

    public void toTriple(int numTicks) {
    	tripleTicks = numTicks;
    }

    public void toFreeze(int numTicks) {
    	freezeTicks = numTicks;
    }

    public void toImmune(int numTicks) {
    	immuneTicks = numTicks;
    }

    // Updates active trigger time ticks
    protected void updateTriggers() {
    	if(immuneTicks!=0) --immuneTicks;
    	if(freezeTicks!=0) --freezeTicks;
    	if(tripleTicks!=0) --tripleTicks;
    }

    public void setTRAPPED(){
        TRAPPED = true;
    }

    public void setUNTRAPPED(){
        TRAPPED = false;
    }
    // -----------------------------------------------------------


    public void handle(List<BaseGameEntity> entities) {
        entities.forEach(this::handle);
    }

    public void handle(BaseGameEntity entity) {
        if(entity instanceof GameCharacter && getID() != entity.getID() && intersects(entity)) {
            move(-1);
        } else if(entity instanceof Obstacle && !((Obstacle) entity).getAttributes().contains(Attribute.WALKABLE) && intersects(entity)) {
            move(-1);
        }
    }

    /**
     * Overloads basic <i>move()</i> method with extra speed multiplier parameter
     * <br>TODO: assure speedMultiplier is within [-5, 5]
     * @param speedMultiplier number by which the speed will be multiplied (use negative to inverse movement)
     */
    public void move(double speedMultiplier) {
        velocity = velocity.multiply(speedMultiplier);
        x += velocity.getX();
        y += velocity.getY();
    }

    @Override
    public void move() {
        Point2D force = steering.calculate();
        Point2D acceleration = force.multiply(1./mass);
        //debug
        this.acceleration = acceleration;

        velocity = velocity.add(acceleration);
        if(velocity.magnitude()> maxSpeed){
            velocity = velocity.normalize().multiply(maxSpeed);
        }
        if (!isZero(velocity)) {
            heading = velocity.normalize();
        }

        x += velocity.getX();
        y += velocity.getY();
    }

    protected abstract void destroy();

    @Override
    public abstract Circle getHitBox();

    /**
     * Gets radius of a circular hit-box
     * @return radius of a character's hit-box
     */
    public double getHitBoxRadius() {
        return getHitBox().getRadius();
    }

    /**
     *For path smoothing debug
     */
    abstract public List<Shape> getSmoothingBoxes();
    public void teleport(double x,double y){
        this.x = x;
        this.y = y;
    }
    public void destroyed(){
        this.hp-=200;
    }

    public int getImmuneTicks() {
        return immuneTicks;
    }
}

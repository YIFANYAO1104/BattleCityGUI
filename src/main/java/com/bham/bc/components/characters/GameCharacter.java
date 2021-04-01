package com.bham.bc.components.characters;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.Direction;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class GameCharacter extends MovingEntity {
    private final double MAX_HP;
    protected double hp;
    protected Side side;
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
    public void changeHP(double health) {
        hp = Math.min(hp + health, MAX_HP);
        if(hp <= 0) destroy();
    }

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


    /**
     * Handles bullet collision - takes damage and destroys bullet
     * @param bullet bullet to handle
     */
    protected void handle(Bullet bullet) {
        if(intersects(bullet)) {
            if(bullet.getSide() != side && immuneTicks == 0) {
                changeHP(-bullet.getDamage());
            }
            bullet.destroy();
        }
    }

    /**
     * Handles character collision - moves back
     * @param gameCharacter character to handle
     */
    protected void handle(GameCharacter gameCharacter) {
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
        gameCharacters.forEach(this::handle);
        bullets.forEach(this::handle);
    }

    public void handleAll(List<BaseGameEntity> en1){
        en1.forEach(b1 -> {
            try {
                handle((Bullet) b1);
                handle((GameCharacter)b1);
            }catch (Exception e){}
        });
    }


    /**
     * Overloads basic <i>move()</i> method with extra speed multiplier parameter
     * <br>TODO: assure speedMultiplier is within [-5, 5]
     * @param speedMultiplier number by which the speed will be multiplied (use negative to inverse movement)
     */
    public void move(double speedMultiplier) {
        x += Math.sin(Math.toRadians(angle)) * speed * speedMultiplier;
        y -= Math.cos(Math.toRadians(angle)) * speed * speedMultiplier;
    }

    @Override
    public void move() {
        x += Math.sin(Math.toRadians(angle)) * speed;
        y -= Math.cos(Math.toRadians(angle)) * speed;
    }

    protected abstract void destroy();

    /**
     *For path smoothing debug
     */
    abstract public List<Shape> getSmoothingBoxes();
    public void teleport(double x,double y){
        this.x = x;
        this.y =y;
    }
    public void destroyed(){
        this.hp-=200;
    }
}

package com.bham.bc.components.characters;

import com.bham.bc.audio.SoundEffect;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.triggers.powerups.Weapon;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.Constants;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.physics.CollisionHandler;
import com.bham.bc.utils.GeometryEnhanced;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.services;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class GameCharacter extends MovingEntity {
    public static final int MAX_SIZE = 32;

    private double MAX_HP;
    protected double hp;
    protected Side side;
    protected TargetingSystem targetingSystem;


    protected int immuneTicks, freezeTicks, tripleTicks = 0;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     * @param speed value which defines the initial velocity
     */
    protected GameCharacter(double x, double y, double speed, double hp, Side side) {
        super(x, y, speed);
        assert (speed <= Constants.MAX_CHARACTER_SPEED) : "<GameCharacter::Constructor>: invalid speed";
        assert (hp <= Constants.MAX_CHARACTER_HEALTH) : "<GameCharacter::Constructor>: invalid hp";
        MAX_HP = hp;
        this.hp = hp;
        this.side = side;
        targetingSystem = new TargetingSystem(this);

        mass = 3;
    }

    public NavigationService getNavigationService(){
        return null;
    }


    public TargetingSystem getTargetingSystem() {
        return targetingSystem;
    }

    /**
     * Gets the HP of the character
     * @return current HP
     */
    public double getHp() {
        return hp;
    }

    /**
     * Gets the MAX HP of the character
     * @return initial HP (which is MAX) the character was assigned with
     */
    public double getMaxHp() {
        return MAX_HP;
    }

    /**
     * Gets character's side
     * @return ALLY or ENEMY side the character belongs to
     */
    public Side getSide() {
        return side;
    }

    /**
     * Increases or decreases HP for the character
     * @param health amount by which the character's HP is changed
     */
    public void changeHp(double health) {
        hp = Math.min(hp + health, MAX_HP);

        if(hp <= 0) {
            audioManager.playEffect(SoundEffect.DESTROY_CHARACTER);
            if(side == Side.ENEMY) services.changeScore(50);
            destroy();
        }
    }

    /**
     * Get immune activation time
     * @return immune activation time (if it's 0 meaning character is not in immune state)
     */
    public int getImmuneTicks() {
        return immuneTicks;
    }
    public void speedUp(double x){
        this.maxSpeed=x;
    }

    // TEMP: DOCUMENT ------------------------------------------------
    @Deprecated
    public void switchWeapon(Weapon w) {}

    public void toTriple(int numTicks) {
        tripleTicks = numTicks;
    }
    public void toFreeze(int numTicks) {
        this.freezeTicks = numTicks;
    }
    public void toImmune(int numTicks) {
        immuneTicks = numTicks;
    }
    protected void updateTriggers() {
        if(immuneTicks!=0) --immuneTicks;
        if(freezeTicks!=0) --freezeTicks;
        if(tripleTicks!=0) --tripleTicks;
    }
    public void destroyed(){
        this.hp-=200;
    }

    public void teleport(double x,double y){
        this.x = x;
        this.y = y;
    }
    // -----------------------------------------------------------

    /**
     * Handles collision of one {@link BaseGameEntity} object
     *
     * <p>This method handles character's collision for 2 base game entities.</p>
     * <ul>
     *     <li>{@link GameCharacter} - for this object, the character performs 2 collision resolves: one for impulse collision
     *     and another one for the continuous collision when the characters push one another</li>
     *     <li>{@link Obstacle} - for this object, if it doesn't an attribute <i>WALKABLE</i>, the character is simply moved
     *     back by the same velocity it bumped to this obstacle</li>
     * </ul>
     *
     * @param entity a generic entity that is converted to appropriate child instance the character will handle
     * @see CollisionHandler
     */
    public void handle(BaseGameEntity entity) {
        if(entity instanceof GameCharacter && getID() != entity.getID() && intersects(entity)) {
            CollisionHandler.resolveElasticCollision(this, (GameCharacter) entity);
            CollisionHandler.resolveContinuousCollision(this, (GameCharacter) entity);
        } else if(entity instanceof Obstacle && !((Obstacle) entity).getAttributes().contains(Attribute.WALKABLE) && intersects(entity)) {
            move(-1);
        }
    }

    /**
     * Handles intersection of multiple {@link BaseGameEntity} objects
     * @param entities a list of entities the character's collision will be checked on
     * @see #handle(BaseGameEntity)
     */
    public void handle(List<BaseGameEntity> entities) {
        entities.forEach(this::handle);
    }

    /**
     * Overloads basic <i>move()</i> method with extra speed multiplier parameter
     * @param speedMultiplier number by which the speed will be multiplied (use negative to inverse movement)
     */
    public void move(double speedMultiplier) {
        velocity = velocity.multiply(speedMultiplier);
        x += velocity.getX();
        y += velocity.getY();
    }

    /**
     * Unregisters and prepares to remove the character. Also runs any destruction effects
     */
    protected abstract void destroy();
    public void armorUP(double max){
        this.MAX_HP =max;
        this.hp = max;
        System.out.println("The max HP of character has been changed to " + hp);
    }

    /**
     * Gets radius of a circular hit-box
     * @return radius of a character's hit-box
     */
    @Override
    public double getHitBoxRadius() {
        return getHitBox().getRadius();
    }

    /**
     * TODO: remove?
     * Gets a list of path areas (used for path smoothing)
     * @return a list of areas of type Shape
     */
    abstract public List<Shape> getSmoothingBoxes();

    @Override
    public void render(GraphicsContext gc) {
        drawRotatedImage(gc, entityImages[0], getAngle());
    }

    @Override
    public abstract Circle getHitBox();



    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.Msg.id){
            case 0:
                System.out.println("you are defeated by id " + msg.Sender);
                return true;
            case 3:
                System.out.println("change the direction");
                return true;
            default:
                System.out.println("no match");
                return false;
        }
    }

    public boolean isReached(Point2D target){
//        return getCenterPosition().distance(target) < 1;
        return intersects(new Circle(target.getX(), target.getY(), 3));
    }

    public void brake(){
        velocity=new Point2D(0,0);
    }
}

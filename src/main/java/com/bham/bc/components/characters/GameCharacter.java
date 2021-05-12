package com.bham.bc.components.characters;

import com.bham.bc.audio.SoundEffect;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.HitMarker;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.ai.TargetingSystem;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.physics.CollisionHandler;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.util.List;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.services;

/**
 * Represents a character - this includes enemies, players and AI companions
 */
abstract public class GameCharacter extends MovingEntity {
    /** Maximum width and height of any character */
    public static final int MAX_SIZE = 32;

    /** Maximum health of any character */
    public static final double MAX_HP = 300;

    /** Maximum speed of any character */
    public static final double MAX_SPEED = 5;

    /** Current <i>full</i> hp value possible for the character (<b>note:</b> not current hp) */
    protected double fullHp;

    /** Current hp value the character has */
    protected double hp;

    /** {@code ALLY} or {@code ENEMY} side the character belongs to */
    protected Side side;

    /** Targeting system to obtain possible targets */
    protected TargetingSystem targetingSystem;

    /**
     * Trigger(s) activation time
     * <p> 0 means trigger is not active or character is not in corresponding trigger state <p>
     */
    protected int isImmune, isFreeze, isTriple, isInverse = 0;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     * @param speed value which defines the initial velocity
     */
    protected GameCharacter(double x, double y, double speed, double hp, Side side) {
        super(x, y, speed);
        assert -MAX_SPEED <= speed && speed <= MAX_SPEED : "<GameCharacter::Constructor>: invalid speed";
        assert 0 <= hp && hp <= MAX_HP : "<GameCharacter::Constructor>: invalid hp";
        fullHp = hp;
        this.hp = hp;
        this.side = side;
        targetingSystem = new TargetingSystem(this);

        mass = 3;
    }

    /**
     * Renders the health bar below character
     * @param gc graphics context to render the hp bar
     */
    protected void renderHp(GraphicsContext gc) {
        // Declare the global colors used in the CSS file
        String FG_1 = "#135ADD";  // -fx-primary-color (foreground primary)
        String BG_1 = "#080A1E";  // -fx-bg-color (background primary)

        // Set the current active health of the character
        Stop[] healthBarStops = new Stop[]{ new Stop(hp/ fullHp, Color.web(FG_1)), new Stop(hp/ fullHp, Color.web(BG_1)) };
        LinearGradient healthBarGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, healthBarStops);

        // Set the fill nad the stroke of the health bar
        gc.setFill(healthBarGradient);
        gc.setStroke(Color.web(BG_1));
        gc.setLineWidth(1);

        // Set the width (e.g. dependent on hp or on character's width)
        // double barWidth = MAX_SIZE;
        double barWidth = fullHp /3;

        // Draw the health bar
        gc.fillRect(getCenterPosition().getX() - barWidth*.5, getCenterPosition().getY() + MAX_SIZE*.5, barWidth, 3);
        gc.strokeRect(getCenterPosition().getX() - barWidth*.5, getCenterPosition().getY() + MAX_SIZE*.5, barWidth, 3);
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
     * Gets the full possible HP of the character
     * @return initial HP (which is full) the character was assigned with
     */
    public double getFullHp() {
        return fullHp;
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
        hp = Math.min(hp + health, fullHp);
        Trigger hitMarker = new HitMarker(this, entityImages[0], getAngle());
        services.addTrigger(hitMarker);

        if(hp <= 0) {
            audioManager.playEffect(SoundEffect.DESTROY_CHARACTER);
            if(side == Side.ENEMY) services.changeScore(50);
            destroy();
        }
    }

    /**
     * Get immune activation time
     * @return {@link #isImmune}
     */
    public int isImmune() {
        return isImmune;
    }
    
    /**
    * Increase maximum speed
    * @param x maximum speed to be set
    */
    public void speedUp(double x){
        this.maxSpeed=x;
    }

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

    /**
     * Increase character health and maximum health
     * @param max new character maximum healt
     */
    public void armorUP(double max){
        this.fullHp =max;
        this.hp = max;
    }

    /**
     * Gets radius of a circular hit-box
     * @return radius of a character's hit-box
     */
    @Override
    public double getHitBoxRadius() {
        return getHitBox().getRadius();
    }

    @Override
    public void render(GraphicsContext gc) {
        drawRotatedImage(gc, entityImages[0], getAngle());
        renderHp(gc);
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

    /**
     * Judge whether an agent is close enough to a position
     * @param target the position an agent want to reach
     * @return true if close enough
     */
    public boolean isReached(Point2D target){
        return intersects(new Circle(target.getX(), target.getY(), 3));
    }

    /**
     * set a character's velocity to zero vector
     */
    public void brake(){
        velocity=new Point2D(0,0);
    }

    /**
     * get the max damage from a character
     * should be constant
     * @return the max damage
     */
    public double getMaxDamage() {
        return 0;
    }

    /**
     * get the min damage from a character
     * should be constant
     * @return the min damage
     */
    public double getMinDamage(){
        return 0;
    }
}

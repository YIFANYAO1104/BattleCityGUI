package com.bham.bc.components.shooting;

import com.bham.bc.audio.SoundEffect;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;

import java.util.List;

import static com.bham.bc.audio.AudioManager.audioManager;


/**
 * Represents any bullet and defines common bullet properties
 */
abstract public class Bullet extends MovingEntity {
    private final BulletType TYPE;
    private final Side SIDE;
    private double damage;

    /**
     * <p>Constructs a bullet using with geometrical properties to determine the position of a bullet on each frame.
     * SIDE property is used to indicate which team the bullet can damage. </p>
     *
     * @param x       top left position in x axis
     * @param y       top left position in y axis
     * @param speed   velocity value at which the bullet will move
     * @param heading a normalized vector indicating the direction the bullet is moving
     * @param type    bullet type (e.g., DEFAULT, EXPLOSIVE)
     * @param side    ALLY or ENEMY side the bullet belongs to
     * @param damage  amount of hp the bullet can take from an entity
     */
    public Bullet(double x, double y, double speed, Point2D heading, BulletType type, Side side, double damage) {
        super(x, y, speed, heading);
        this.TYPE = type;
        this.SIDE = side;
        this.damage = damage;

        entityImages = new Image[] { TYPE.getImage() };
    }

    /**
     * Gets bullet's type
     * @return DEFAULT or EXPLOSIVE type the bullet belongs to
     */
    public BulletType getType() {
        return TYPE;
    }

    /**
     * Gets bullet's side
     * @return ALLY or ENEMY side the bullet belongs to
     */
    public Side getSide() {
        return SIDE;
    }

    /**
     * Gets bullet's damage
     * @return amount of damage the bullet deals
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets bullet's damage
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Handles collision of one {@link BaseGameEntity} object
     *
     * <p>This method hendles bullet's collision for 2 base game entities.</p>
     * <ul>
     *     <li>{@link GameCharacter} - for this object, the bullet simply deduces some hp based on its damage and is destroyed</li>
     *     <li>{@link Obstacle} - for this object, if it has an attribute <i>WALL</i>, the bullet is destroyed and if it has an
     *     attribute <i>BREAKABLE</i>, the bullet deduces some health pints from it based on its damage</li>
     * </ul>
     *
     * @param entity a generic entity that is converted to appropriate child instance the bullet will handle
     */
    public void handle(BaseGameEntity entity) {
        if(entity instanceof GameCharacter && intersects(entity) && getSide() != ((GameCharacter) entity).getSide() && ((GameCharacter) entity).getImmuneTicks() == 0) {
            ((GameCharacter) entity).changeHp(-damage);
            audioManager.playEffect(SoundEffect.HIT_CHARACTER);
            destroy();
        } else if(entity instanceof Obstacle && ((Obstacle) entity).getAttributes().contains(Attribute.WALL) && intersects(entity)) {
            destroy();
            if(((Obstacle) entity).getAttributes().contains(Attribute.BREAKABLE)) {
                ((Obstacle) entity).changeHp(-damage);
                audioManager.playEffect(SoundEffect.HIT_SOFT);
            } else {
                audioManager.playEffect(SoundEffect.HIT_HARD);
            }
        }
    }

    /**
     * Handles intersection of multiple {@link BaseGameEntity} objects
     * @param entities a list of entities the bullet's collision will be checked on
     * @see #handle(BaseGameEntity)
     */
    public void handle(List<BaseGameEntity> entities) {
        entities.forEach(this::handle);
    }

    /**
     * Unregisters and prepares to remove the bullet. Also runs any destruction effects
     */
    public abstract void destroy();

    @Override
    public void render(GraphicsContext gc) {
        drawRotatedImage(gc, entityImages[0], getAngle());
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return "Bullet";
    }
}
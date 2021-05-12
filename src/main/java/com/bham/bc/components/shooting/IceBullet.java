package com.bham.bc.components.shooting;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents a ice bullet the player starts with
 */
public class IceBullet extends Bullet {
    public static final BulletType TYPE = BulletType.ICE;
    public static final double SPEED = 5;
    public static final double DAMAGE = 10;

    /**
     * Constructs a bullet using ice bullet's attributes for speed and damage
     *
     * @param x       top left position in x axis
     * @param y       top left position in y axis
     * @param heading a normalized vector indicating the direction the ice bullet is moving
     * @param side    ALLY or ENEMY side the bullet belongs to
     */
    public IceBullet(double x, double y, Point2D heading, Side side) {
        super(x, y, SPEED, heading, TYPE, side, DAMAGE);

    }

    /**
     * Bullet get destroyed.
     */
    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;
    }

    /**
     * The handle method of Bullet, dispose various situations when the bullets collides with other entities.
     *
     * @param entity a generic entity that is converted to appropriate child instance the bullet will handle
     */
    @Override
    public void handle(BaseGameEntity entity) {

        if(entity instanceof GameCharacter && intersects(entity) && getSide() != ((GameCharacter) entity).getSide()) {
            if (((GameCharacter) entity).isImmune() == 0)((GameCharacter) entity).changeHp(-DAMAGE);
            destroy();
        } else if(entity instanceof Obstacle && ((Obstacle) entity).getAttributes().contains(Attribute.WALL) && intersects(entity)) {
            destroy();
            if(((Obstacle) entity).getAttributes().contains(Attribute.BREAKABLE)) {
                ((Obstacle) entity).changeHp(-DAMAGE);
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        for(int i =0; i< entityImages.length;i++){
            drawRotatedImage(gc, entityImages[i], getAngle());
        }
    }

    @Override
    public void setTime() {

    }

    /**
     * The movement of the bullet.
     */
    @Override
    public void move() {
        x += velocity.getX();
        y += velocity.getY();

        if (x < 0 || y < 0 || x > GameMap.getWidth() || y > GameMap.getHeight()) {
            entityManager.removeEntity(this);
            exists = false;
        }
    }

    /**
     * The update of bullet which calls {@link  #move()}
     */
    @Override
    public void update() {
        move();
    }

    /**
     * Generate the hitbox of current position.
     *
     * @return the rectangle hitbox
     */
    @Override
    public Rectangle getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, getSize().getX(), getSize().getY());
        hitBox.getTransforms().add(new Rotate(getAngle(), x + getSize().getX()/2,y + getSize().getY()/2));

        return hitBox;
    }

    @Override
    public double getHitBoxRadius() {
        return Math.hypot(getHitBox().getWidth()/2, getHitBox().getHeight()/2);
    }
}

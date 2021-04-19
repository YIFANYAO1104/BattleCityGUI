package com.bham.bc.components.shooting;


import com.bham.bc.components.characters.Side;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.view.GameSession.FRAME_RATE;

public class ExplosiveBullet extends Bullet {
    public static final BulletType TYPE = BulletType.EXPLOSIVE;
    public static final double DAMAGE = 100;

    public static final int MAX_BOUND_WIDTH = 300;
    public static final int MAX_BOUND_HEIGHT = 300;

    public static final int LIFETIME = FRAME_RATE;

    private int existTime;

    private Rectangle hitBox;

    /**
     * Constructs a bullet using default bullet's attributes for speed and damage
     *
     * @param x       top left position in x axis
     * @param y       top left position in y axis
     * @param heading a normalized vector indicating the direction the bullet is moving
     * @param side    ALLY or ENEMY side the bullet belongs to
     */
    public ExplosiveBullet(double x, double y, Point2D heading, Side side) {
        super(x, y, 0, heading, TYPE, side, DAMAGE);
        existTime = 0;
        hitBox = updateHitBox(existTime);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void move() { }

    @Override
    public void update() {
        existTime++;
        hitBox = updateHitBox(existTime);
        if (existTime >= LIFETIME) {
            exists = false;
            entityManager.removeEntity(this);
        }
    }

    @Override
    public Rectangle getHitBox() {
        return hitBox;
    }

    private Rectangle updateHitBox(int existTime) {
        //get bound rectangle top left pos
        //calculate region radius according to current time
        Point2D regionRadius = new Point2D(MAX_BOUND_WIDTH, MAX_BOUND_HEIGHT).multiply(existTime/(double)LIFETIME);
        Point2D topLeft = getPosition().subtract(regionRadius.subtract(getSize()).multiply(0.5));

        //create hit box according to bound rectangle
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), regionRadius.getX(), regionRadius.getY());
        hitBox.getTransforms().add(new Rotate(getAngle(), topLeft.getX() + regionRadius.getX() /2,topLeft.getY() + regionRadius.getY() /2));

        return hitBox;
    }

    @Override
    public double getHitBoxRadius() {
        Point2D p1 = new Point2D(MAX_BOUND_WIDTH, MAX_BOUND_HEIGHT);
        return Math.sqrt(p1.getX()/2 * p1.getX()/2 + p1.getY()/2*p1.getY()/2);
    }
}
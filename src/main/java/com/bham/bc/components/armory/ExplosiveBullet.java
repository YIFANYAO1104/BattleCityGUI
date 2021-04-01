package com.bham.bc.components.armory;


import com.bham.bc.components.characters.Side;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.Constants.FRAME_RATE;

public class ExplosiveBullet extends Bullet {
    public static final BulletType TYPE = BulletType.EXPLOSIVE;
    public static final double DAMAGE = 100;

    public static final int MAX_BOUNDWIDTH = 300;
    public static final int MAX_BOUNDHEIGHT = 300;



    public static final int LIFETIME = 1*FRAME_RATE;

    private int existTime;

    private Rectangle hitBox;

    /**
     * Constructs a bullet using default bullet's attributes for speed and damage
     *
     * @param x      top left position in x axis
     * @param y      top left position in y axis
     * @param angle  angle at which the bullet will move
     * @param side   ALLY or ENEMY side the bullet belongs to
     */
    public ExplosiveBullet(double x, double y, double angle, Side side) {
        super(x, y, 0, angle, TYPE, side, DAMAGE);
        existTime = 0;
        hitBox = updateHitBox(existTime);
    }

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;
    }

    @Override
    public void move() { }

    @Override
    public void update() {
        existTime++;
        hitBox = updateHitBox(existTime);
        if (existTime >= LIFETIME) destroy();
    }

    @Override
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

    @Override
    public Rectangle getHitBox() {
        return hitBox;
    }

    private Rectangle updateHitBox(int existTime) {
        //get bound rectangle top left pos
        //calculate region radius according to current time
        Point2D regionRadius = new Point2D(MAX_BOUNDWIDTH, MAX_BOUNDHEIGHT).multiply(existTime/(double)LIFETIME);
        Point2D topLeft = getPosition().subtract(regionRadius.subtract(getRadius()).multiply(0.5));

        //create hit box according to bound rectangle
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), regionRadius.getX(), regionRadius.getY());
        hitBox.getTransforms().add(new Rotate(angle, topLeft.getX() + regionRadius.getX() /2,topLeft.getY() + regionRadius.getY() /2));

        return hitBox;
    }
}
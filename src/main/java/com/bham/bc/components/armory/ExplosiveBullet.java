package com.bham.bc.components.armory;


import com.bham.bc.components.characters.SIDE;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.Constants.FRAME_RATE;

public class ExplosiveBullet extends Bullet {

    public static final String IMAGE_PATH = "file:src/main/resources/img/armory/defaultBullet.png";
    public static final int WIDTH = 6;
    public static final int HEIGHT = 12;

    public static final int MAX_BOUNDWIDTH = 300;
    public static final int MAX_BOUNDHEIGHT = 300;

    public static final double DAMAGE = 100;

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
    public ExplosiveBullet(double x, double y, double angle, SIDE side) {
        super(x, y, 0, angle, side, DAMAGE);
        entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
        existTime = 0;
        this.hitBox = updateHitBox(existTime);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void move() {
//        x += Math.sin(Math.toRadians(angle)) * speed;
//        y -= Math.cos(Math.toRadians(angle)) * speed;
//
//        if (x < 0 || y < 0 || x > GameMap.getWidth() || y > GameMap.getHeight()) {
//            entityManager.removeEntity(this);
//            exists = false;
//        }
    }

    @Override
    public void update() {
        existTime++;
        this.hitBox = updateHitBox(existTime);
        if (existTime >= LIFETIME) {
            entityManager.removeEntity(this);
            exists = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], getAngle()); }

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
        hitBox.getTransforms().add(new Rotate(getAngle(), topLeft.getX() + regionRadius.getX() /2,topLeft.getY() + regionRadius.getY() /2));

        return hitBox;
    }
}
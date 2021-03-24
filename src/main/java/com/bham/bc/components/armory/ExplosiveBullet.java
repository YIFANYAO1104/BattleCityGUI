package com.bham.bc.components.armory;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerRegionRectangle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.Constants.FRAME_RATE;

public class ExplosiveBullet extends Bullet {

    public static final String IMAGE_PATH = "file:src/main/resources/img/armory/defaultBullet.png";
    public static final int WIDTH = 6;
    public static final int HEIGHT = 12;

    public static final int BOUNDWIDTH = 300;
    public static final int BOUNDHEIGHT = 300;

    public static final double SPEED = 5;
    public static final double DAMAGE = 100;

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
    }

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;
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
    public void update() { move(); }

    @Override
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

    @Override
    public Rectangle getHitBox() {
        //get bound rectangle top left pos
        Point2D regionRadius = new Point2D(BOUNDWIDTH,BOUNDHEIGHT);
        Point2D topLeft = getPosition().subtract(regionRadius.subtract(getRadius()).multiply(0.5));

        //create hit box according to bound rectangle
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), BOUNDWIDTH, BOUNDHEIGHT);
        hitBox.getTransforms().add(new Rotate(angle, topLeft.getX() + BOUNDWIDTH/2,topLeft.getY() + BOUNDHEIGHT/2));

        return hitBox;
    }


}
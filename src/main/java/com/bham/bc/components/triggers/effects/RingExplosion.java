package com.bham.bc.components.triggers.effects;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

/**
 * Represents Kamikaze's explosion effect. This is created as a trigger because it affects player's HP
 */
public class RingExplosion extends Trigger {

    public static final int SIZE = 60;
    private int currentFrame;
    private double damage;
    private Side side;

    /**
     * Constructs explosion at a given location
     * @param centerPosition x and y coordinates of the trigger image
     * @param damage amount of damage that will be dealt to specific side
     * @param side ALLY or ENEMY side trigger belongs to
     */
    public RingExplosion(Point2D centerPosition, double damage, Side side) {
        super((int) (centerPosition.getX() - SIZE/2), (int) (centerPosition.getY() - SIZE/2));
        this.damage = damage;
        this.side = side;
        currentFrame = 0;
    }

    /**
     * Initializes all the images for the explosion
     */
    protected Image[] getDefaultImage() {
        String baseUrl = "file:src/main/resources/img/triggers/effects/blueRingExplosion";
        entityImages = new Image[19];

        for(int i = 1; i <= 19; i++) {
            String url = baseUrl + i + ".png";
            entityImages[i-1] = new Image(url, SIZE, SIZE, false, false);
        }
        return entityImages;
    }

    @Override
    public Circle getHitBox() {
        // f(x) = -x^2/250 + .8
        double y = -Math.pow((currentFrame - 14), 2)/250 + .8;
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0 * y);
    }

    @Override
    public double getHitBoxRadius() {
        return getHitBox().getRadius();
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(entityImages[currentFrame++ % 19], x, y);
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity) && ((GameCharacter) entity).getSide() != side) {
            active = false;
            ((GameCharacter) entity).changeHp(-damage);
        }
    }

    @Override
    public void update() {
        if(currentFrame == 19) exists = false;
    }

    @Override
    public String toString() {
        return "Ring explosion";
    }
}

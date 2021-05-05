package com.bham.bc.components.shooting;

import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents a default laser the player starts with
 */
public class LaserGun extends Bullet {

    int index =0;
    int time;

    /**
     * Constructs a laser ,which is considered as default laser of player
     *
     * @param x       top left position in x axis
     * @param y       top left position in y axis
     * @param heading a normalized vector indicating the direction the laser is shooting
     * @param side    ALLY or ENEMY side the laser belongs to
     */
    public LaserGun(double x, double y, Point2D heading, Side side) {
        super(x, y, 10, heading, BulletType.DefaultLaser, side, 10);
        try{
        this.entityImages = new Image[]{
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                //(new Image(getClass().getClassLoader().getResourceAsStream("img/tiles/triggers/flash.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l4.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l5.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l6.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l6.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l7.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l7.png"))),
               // (new Image(getClass().getClassLoader().getResourceAsStream("img/tiles/triggers/flash.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l5.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l4.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
        };
        }catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }

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

    /**
     * update the dynamic effect of laser
     */
    public void updateLaser(){
        if(time > 0) time--;
        else {entityManager.removeEntity(this);
        exists=false;}
    }

    /**
     * Initialize the dynamic effect time of laser
     */
    public void setTime(){
        time = 10;
    }

    /**
     * update method to call {@link #update()}
     */
    @Override
    public void update() {

        updateLaser();
    }
    /**
     * Laser get destroyed.
     */
    @Override
    public void destroy() {


    }
    @Override
    public void render(GraphicsContext gc) {
        exists=true;
        drawRotatedImage(gc,entityImages[index%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());
        drawRotatedImage(gc,entityImages[index++%entityImages.length],getAngle());


    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void move() {
        return;
    }
}

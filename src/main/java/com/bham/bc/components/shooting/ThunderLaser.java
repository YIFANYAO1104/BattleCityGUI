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


public class ThunderLaser extends Bullet{

    int index =0;
    int time;

    public ThunderLaser(double x, double y, Point2D heading, Side side) {
        super(x, y, 10, heading, BulletType.DefaultLaser, side, 10);
        try{
        this.entityImages = new Image[]{
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl0.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl1.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl2.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl3.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl4.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl4.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl5.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl5.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl6.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl6.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl7.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl7.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl8.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl8.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl9.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl9.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl10.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl10.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl11.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl11.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl10.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl9.png"))),
                //(new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl8.png"))),
                //(new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl7.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl5.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl12.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl12.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl12.png"))),
                (new Image(getClass().getClassLoader().getResourceAsStream("img/shooting/tl12.png"))),


        };}catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public Rectangle getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, getSize().getX(), getSize().getY());
        hitBox.getTransforms().add(new Rotate(getAngle(), x +getSize().getX()/2,y + getSize().getY()/2));

        return hitBox;
    }

    @Override
    public double getHitBoxRadius() {
        return Math.hypot(getHitBox().getWidth()/2, getHitBox().getHeight()/2);
    }

    public void updateLaser(){
        if(time > 0) time--;
        else {entityManager.removeEntity(this);
            exists=false;}
    }
    public void setTime(){
        time = 10;
    }

    @Override
    public void update() {

        updateLaser();

    }

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
//        x += velocity.getX();
//        y += velocity.getY();
//
//        if (x < 0 || y < 0 || x > GameMap.getWidth() || y > GameMap.getHeight()) {
//            entityManager.removeEntity(this);
//            exists = false;
//        }
//
//    }
        return;
    }
}



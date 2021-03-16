package com.bham.bc.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

public abstract class MovingEntity extends BaseGameEntity {
    protected double speed;
    protected double angle;

    protected double oldX, oldY;
    protected int width, length;


    protected Direction direction;
    protected boolean isAlive;

    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     */
    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     * The other attributes of an moving entity will be set
     */
    protected MovingEntity(double x, double y, double speed, int width, int length) {
        super(GetNextValidID(), x, y);
        this.speed = speed;
        this.angle = 0;

        this.oldX = x;
        this.oldY = y;

        this.width = width;
        this.length = length;

        isAlive = true;
    }

    /**
     * Draws an image on a graphics context.
     *
     * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
     * (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
     *
     * @param gc the graphics context the image is to be drawn on.
     * @param angle the angle of rotation.
     * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
     * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
     *
     * @see <a href="https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas">stackoverflow.com</a>
     */
    protected void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save();
        Rotate r = new Rotate(angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, tlpx, tlpy);
        gc.restore();
    }



    public boolean isAlive() { return isAlive; }

    public void setAlive(boolean alive) { this.isAlive = alive; }

    public void changToOldDir() { x = oldX; y = oldY; }




    /**
     *
     */
    protected abstract void move();

    @Override
    public Shape getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, width, length);
        hitBox.getTransforms().add(new Rotate(angle, x + width/2, y + length/2));

        return hitBox;
    }
}

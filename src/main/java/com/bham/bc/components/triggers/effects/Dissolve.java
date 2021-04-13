package com.bham.bc.components.triggers.effects;

import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

/**
 * Represents effect of any image dissolving to particles
 */
public class Dissolve extends Trigger {
    private final double DISSOLVE_SPEED = 5;
    private final int PIXEL_GAP = 2;

    private double angle;
    private ArrayList<Particle> particles;

    /**
     * Constructs a dissolve effect by generating a particle array
     *
     * @param position x and y coordinates of the top-left corner of the image position
     * @param image    the image to be converted to particles
     * @param angle    angle at which the image is rotated
     */
    public Dissolve(Point2D position, Image image, double angle) {
        super((int) position.getX(), (int) position.getY());
        this.angle = angle;
        particles = new ArrayList<>();
        entityImages = new Image[] { image };
        initParticles();
    }

    /**
     * Initializes particle array list
     *
     * <p>This method loops through pixels of the provided image but skips some of them as defined by the
     * <i>PIXEL_GAP</i>. Each color of the pixel (that is not transparent) is used to create a custom
     * particle that will move away from the center on each update.</p>
     */
    private void initParticles() {
        Rotate rotate = new Rotate(angle, getCenterPosition().getX(), getCenterPosition().getY());
        PixelReader pr = entityImages[0].getPixelReader();

        for(int x = 0; x < entityImages[0].getWidth(); x += PIXEL_GAP) {
            for(int y = 0; y < entityImages[0].getHeight(); y += PIXEL_GAP) {
                Color color = pr.getColor(x, y);

                if (color.getOpacity() > .01) {
                    Point2D particlePosition = getCenterPosition().subtract(getRadius().multiply(.5)).add(x, y);
                    particlePosition = rotate.transform(particlePosition.getX(), particlePosition.getY());
                    particles.add(new Particle(particlePosition.getX(), particlePosition.getY(), getCenterPosition().getX(), getCenterPosition().getY(), PIXEL_GAP, DISSOLVE_SPEED, color));
                }
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        particles.forEach(p -> p.render(gc));
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void update() {
        particles.forEach(Particle::update);

        if(particles.stream().noneMatch(Particle::exists)) {
            exists = false;
        }
    }

    @Override
    public void handle(BaseGameEntity entity) { }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[0];
    }

    @Override
    public ItemType getItemType() {
        return null;
    }

    /**
     * <h1>Round pixel particle</h1>
     *
     * <p>This class represents a particle that can move away from a given center coordinate. It slows down and fades
     * out as it moves and when it reaches its opacity minimum, it stops existing.</p>
     */
    private static class Particle {
        private static final double DECELERATION_RATIO = .8;

        private Color color;
        private double x, y;
        private double size;
        private double speed;
        private double moveX;
        private double moveY;
        private boolean exists;

        /**
         * Constructs particle object
         *
         * <p>This constructor calculates an angle between the particle and the center coordinate, generates a
         * random mass which is inverse so that it could be a factor of the speed, and finds the direction at
         * which the particle will be moving (from pivot)</p>
         *
         * @param x       position in x axis
         * @param y       position in y axis
         * @param centerX pivot point the particle is from in x axis
         * @param centerY pivot point the particle is from in y axis
         * @param size    radius of the particle
         * @param speed   velocity of moving to x or y direction. This could be thought of as step size
         * @param color   initial color the particle has
         */
        public Particle(double x, double y, double centerX, double centerY, int size, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.color = color;

            double angle = Math.atan2(y-centerY, x-centerX) + Math.PI/2;
            double inverseMass = Math.random() + .5;

            moveX = Math.sin(angle) * inverseMass;
            moveY = Math.cos(angle) * inverseMass;

            exists = true;
        }

        /**
         * Updates the movement of the particle
         *
         * <p>Both speed and opacity are reduced on each update call by the same deceleration factor. Deceleration factor
         * creates easing out visual effect.</p>
         */
        public void update() {
            if(color.getOpacity() <= .01) {
                exists = false;
            } else {
                x += moveX * speed;
                y -= moveY * speed;
                speed *= DECELERATION_RATIO;
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity() * DECELERATION_RATIO);
            }
        }

        /**
         * Draws a circular arc which represents a particle with the current color at a specified position
         * @param gc graphics context the particle will be drawn on
         */
        public void render(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillArc(x, y, size, size, 0, 360, ArcType.OPEN);
        }

        /**
         * Checks if particle exists
         * @return true if particle can still be seen and false otherwise
         */
        public boolean exists() {
            return exists;
        }
    }
}

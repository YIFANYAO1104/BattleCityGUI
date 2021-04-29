package com.bham.bc.components.triggers.effects;

import com.bham.bc.components.characters.GameCharacter;
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
public class HitMarker extends Trigger {
    private final double ANGLE;
    private final double DISSOLVE_SPEED;
    private final int PIXEL_GAP;
    private final ArrayList<Particle> PARTICLES;
    private GameCharacter gameCharacter;

    /**
     * Constructs a dissolve effect by generating a particle array. Sets dissolve speed to 5 and pixel gap to 2 by default.
     *
     * @param gameCharacter The Game Character entity that called this trigger
     * @param image    the image to be converted to particles
     * @param angle    angle at which the image is rotated
     */
    public HitMarker(GameCharacter gameCharacter, Image image, double angle) {
        super((int) gameCharacter.getCenterPosition().getX(), (int) gameCharacter.getCenterPosition().getY());
        this.gameCharacter = gameCharacter;
        ANGLE = angle;
        DISSOLVE_SPEED = 5;
        PIXEL_GAP = 1;
        PARTICLES = new ArrayList<>();
        entityImages = new Image[] { image };
        initParticles();
    }

    /**
     * Alternate constructor allowing to set the speed of moving particles and the gap of pixels that can be skipped.
     *
     * @param gameCharacter      The Game Character entity that called this trigger
     * @param image         the image to be converted to particles
     * @param angle         angle at which the image is rotated
     * @param dissolveSpeed how fast the particles will move away from the center
     * @param pixelGap      number of pixels that will be skipped when each particle is generated
     */
    public HitMarker(GameCharacter gameCharacter, Image image, double angle, double dissolveSpeed, int pixelGap) {
        super((int) gameCharacter.getCenterPosition().getX(), (int) gameCharacter.getCenterPosition().getY());
        this.gameCharacter = gameCharacter;
        ANGLE = angle;
        DISSOLVE_SPEED = dissolveSpeed;
        PIXEL_GAP = pixelGap;
        PARTICLES = new ArrayList<>();
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
        Rotate rotate = new Rotate(ANGLE, getCenterPosition().getX(), getCenterPosition().getY());
        PixelReader pr = entityImages[0].getPixelReader();

        for(int x = 0; x < entityImages[0].getWidth(); x += PIXEL_GAP) {
            for(int y = 0; y < entityImages[0].getHeight(); y += PIXEL_GAP) {
                Color color = pr.getColor(x, y);
                color = color.rgb(0, 0, 255, color.getOpacity());

                if (color.getOpacity() > .01) {
                    Point2D particlePosition = getCenterPosition().subtract(getSize().multiply(.5)).add(x, y);
                    particlePosition = rotate.transform(particlePosition.getX(), particlePosition.getY());
                    PARTICLES.add(new Particle(particlePosition.getX(), particlePosition.getY(), getCenterPosition().getX(), getCenterPosition().getY(), PIXEL_GAP, gameCharacter, color));
                }
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        PARTICLES.forEach(p -> p.render(gc));
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public double getHitBoxRadius() {
        return 0;
    }

    @Override
    public void update() {
        PARTICLES.forEach(Particle::update);
        exists = PARTICLES.stream().anyMatch(Particle::exists);
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
        private static final double DECELERATION_RATIO = .75;

        private Color color;
        private GameCharacter gameCharacter;
        private double particleX, particleY, offsetX, offsetY;
        private double size;
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
         * @param gameCharacter   The Game Character entity that called this trigger
         * @param color   initial color the particle has
         */
        public Particle(double x, double y, double centerX, double centerY, double size, GameCharacter gameCharacter, Color color) {
            this.particleX = x;
            this.particleY = y;
            this.offsetX = x - centerX;
            this.offsetY = y - centerY;
            this.size = size;
            this.gameCharacter = gameCharacter;
            this.color = color;

            exists = true;
        }

        /**
         * Updates the movement of the particle
         *
         * <p>Both speed and opacity are reduced on each update call by the same deceleration factor. Deceleration factor
         * creates easing out visual effect.</p>
         */
        public void update() {
            if(color.getOpacity() <= .1) {
                exists = false;
            } else {
                particleX = gameCharacter.getCenterPosition().getX() + offsetX;
                particleY = gameCharacter.getCenterPosition().getY() + offsetY;
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity() * DECELERATION_RATIO);
            }
        }

        /**
         * Draws a circular arc which represents a particle with the current color at a specified position
         * @param gc graphics context the particle will be drawn on
         */
        public void render(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillArc(particleX, particleY, size, size, 0, 360, ArcType.OPEN);
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

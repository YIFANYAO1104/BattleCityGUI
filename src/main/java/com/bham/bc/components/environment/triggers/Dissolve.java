package com.bham.bc.components.environment.triggers;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.triggers.Trigger;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Arrays;

public class Dissolve extends Trigger {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy3.png";
    private Point2D position;
    private double angle;
    private ArrayList<Particle> particles;
    private final int PIXEL_GAP = 2;

    public Dissolve(Point2D position, Image image, double angle) {
        super(BaseGameEntity.GetNextValidID(), (int) position.getX(), (int) position.getY());
        this.position = position;
        this.angle = angle;
        particles = new ArrayList<>();

        entityImages = new Image[] { image };
        initParticles();
    }

    private void initParticles() {
        Rotate rotate = new Rotate(angle, getCenterPosition().getX(), getCenterPosition().getY());
        PixelReader pr = entityImages[0].getPixelReader();

        for(int x = 0; x < entityImages[0].getWidth(); x += PIXEL_GAP) {
            for(int y = 0; y < entityImages[0].getHeight(); y += PIXEL_GAP) {
                Color color = pr.getColor(x, y);

                if (color.getOpacity() != 0) {
                    Point2D particlePosition = getCenterPosition().subtract(getRadius().multiply(.5)).add(x, y);
                    particlePosition = rotate.transform(particlePosition.getX(), particlePosition.getY());
                    particles.add(new Particle(particlePosition.getX(), particlePosition.getY(), getCenterPosition().getX(), getCenterPosition().getY(), 5, color));
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        particles.forEach(p -> p.render(gc));
    }

    @Override
    public void tryTriggerC(GameCharacter entity) { }

    @Override
    public void tryTriggerO(GenericObstacle entity) { }

    @Override
    public void update() {
        particles.forEach(Particle::update);
    }



    @Override
    public ItemType getItemType() {
        return null;
    }

    private class Particle {
        private final int SIZE = 2;
        private double x, y;
        private double centerX, centerY;
        private double speed;
        private Color color;
        private double density;
        private double angle;
        private double sin;
        private double cos;


        public Particle(double x, double y, double centerX, double centerY, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.centerX = centerX;
            this.centerY = centerY;
            this.speed = speed;
            this.color = color;
            density = (Math.random() * 10) + 2;
            angle = Math.atan2(y-centerY, x-centerX) + Math.PI/2;
            sin = Math.sin(angle) / density;
            cos = Math.cos(angle) / density;
            //x += Math.sin(Math.toRadians(angle)) * density;
            //y -= Math.cos(Math.toRadians(angle)) * density;
        }

        public void update() {
            x += sin * speed;
            y -= cos * speed;
        }

        public void render(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillArc(x, y, SIZE, SIZE, 0, 360, ArcType.OPEN);
        }
    }
}

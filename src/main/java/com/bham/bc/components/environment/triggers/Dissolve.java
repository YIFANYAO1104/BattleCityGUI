package com.bham.bc.components.environment.triggers;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.triggers.Trigger;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Dissolve extends Trigger {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy3.png";

    public Dissolve(int id, int x, int y) {
        super(id, x, y);
        entityImages = new Image[] { new Image(IMAGE_PATH, 30, 0, true, false) };
    }

    @Override
    public void tryTriggerC(GameCharacter entity) { }

    @Override
    public void tryTriggerO(GenericObstacle entity) { }

    @Override
    public void update() {

    }



    @Override
    public ItemType getItemType() {
        return null;
    }

    private class Particle {
        private Point2D position;
        private Point2D pivot;
        private int size;
        private Color color;
        private double density;
        private double angle;


        public Particle(Point2D position, Point2D pivot, int size, Color color) {
            this.position = position;
            this.pivot = pivot;
            this.size = size;
            this.color = color;
            density = (Math.random() * 10) + 2;
            angle = Math.toDegrees(Math.atan2(position.getY() - pivot.getY(), position.getX()) - pivot.getY()) + 90;

            x += Math.sin(Math.toRadians(angle)) * density;
            y -= Math.cos(Math.toRadians(angle)) * density;
        }
    }
}

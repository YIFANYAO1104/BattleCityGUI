package com.bham.bc.entity.ai;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import com.bham.bc.components.characters.Character;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Condition for checking whether a straight line between two hitboxes meets any obstacles in a way
 */
public class CollideCondition implements Condition {
    private int id1;
    private int id2;
    private Line line;
    private Rectangle rect;

    public CollideCondition(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        this.line = new Line();
        rect = new Rectangle();
    }

    public Shape getLine() {
        return rect;
    }

    public void setTestValues(Point2D pt1, Point2D pt2) {
        double an = pt1.angle(pt2);
        an = pt2.angle(pt1) * (pt2.getX() > 0 ? 1 : -1);
        an = Math.atan2(pt2.getY() - pt1.getY(), pt2.getX() - pt1.getX());



        Rectangle hitBox = new Rectangle(pt1.getX(), pt1.getY(), pt1.distance(pt2), 1);
        hitBox.getTransforms().add(new Rotate(Math.toDegrees(an), pt1.getX(),pt1.getY()));
        rect = hitBox;

        line.setStartX(pt1.getX());
        line.setStartY(pt1.getY());
        line.setEndX(pt2.getX());
        line.setEndY(pt2.getY());
    }

    @Override
    public boolean test() {
        List<Character> characters = backendServices.getCharacters().stream().filter(c -> c.getID() != id1 && c.getID() != id2).collect(Collectors.toList());
        boolean val1 = backendServices.intersectsCharacters(line, characters);
        boolean val2 = backendServices.intersectsObstacles(rect);

        System.out.println(val2);

        return val1 && val2;
    }
}

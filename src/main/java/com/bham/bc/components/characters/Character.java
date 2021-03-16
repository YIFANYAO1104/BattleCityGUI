package com.bham.bc.components.characters;

import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.entity.Direction;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;

import java.util.EnumSet;


abstract public class Character extends MovingEntity {

    protected EnumSet<Direction> directionSet;
    /**
     * The constructor of this class will generate a valid ID using parent class's generating ID method
     * The other attributes of an moving entity will be set
     *
     * @param x
     * @param y
     * @param speed
     * @param width
     * @param length
     */
    protected Character(double x, double y, double speed, int width, int length) {
        super(x, y, speed, width, length);
        directionSet = EnumSet.noneOf(Direction.class);
    }


    public Point2D getPosition() { return new Point2D(x, y); }

    public Point2D getRadius() { return new Point2D(width, length); }

    abstract public void increaseHealth(int health);

    abstract public void switchWeapon(Weapon w);
}

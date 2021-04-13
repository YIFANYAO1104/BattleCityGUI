package com.bham.bc.entity.physics;

import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;

/**
 * Represents a class which handles some intersections of moving entities
 */
public class CollisionHandler {

    /**
     * Default constructor. Usually we use this class' static methods
     */
    public CollisionHandler() {}

    /**
     * Performs elastic collision modification
     *
     * <p>Calculates the velocities of 2 entities after an impulse collision taking into account their velocity and mass.
     * This method rotates the velocity vectors, performs <i>One-dimensional Newtonian</i> calculation, rotates the
     * vectors back and sets te new values for both entities.</p>
     *
     * @param entity1 1st entity colliding
     * @param entity2 2nd entity colliding
     */
    public static void resolveElasticCollision(MovingEntity entity1, MovingEntity entity2) {
        Point2D deltaVelocity = entity1.getVelocity().subtract(entity2.getVelocity());
        Point2D deltaDistance = entity2.getCenterPosition().subtract(entity1.getCenterPosition());

        if((deltaVelocity.dotProduct(deltaDistance) >= 0)) {
            double angle = -Math.atan2(deltaDistance.getY(), deltaDistance.getX());

            double mass1 = entity1.getMass();
            double mass2 = entity1.getMass();

            Point2D u1 = rotate(entity1.getVelocity(), angle);
            Point2D u2 = rotate(entity2.getVelocity(), angle);

            Point2D v1 = new Point2D(u1.getX() * (mass1 - mass2) / (mass1 + mass2) + u2.getX() * 2 * mass2 / (mass1 + mass2), u1.getY());
            Point2D v2 = new Point2D(u2.getX() * (mass1 - mass2) / (mass1 + mass2) + u1.getX() * 2 * mass1 / (mass1 + mass2), u2.getY());

            v1 = rotate(v1, -angle);
            v2 = rotate(v2, -angle);

            entity1.setVelocity(v1);
            entity2.setVelocity(v2);
        }
    }

    /**
     * Performs continuous collision modification
     *
     * <p>This method appends acceleration to the 2nd entity to move it from the colliding point. It checks for the direction
     * the center positions of both entities and pushes the second entity from the 1st entity's center (the same can happen
     * for the 1st entity with respect to the 2nd one). This is useful when an impulse has ended but entities are pushing one
     * another.</p>
     *
     * @param entity1 1st entity colliding
     * @param entity2 2nd entity colliding
     */
    public static void resolveContinuousCollision(MovingEntity entity1, MovingEntity entity2) {
        Point2D deltaDistance = entity2.getCenterPosition().subtract(entity1.getCenterPosition());
        Point2D force = deltaDistance.normalize().multiply(-3 * Steering.FRICTION);
        Point2D acceleration = force.multiply(1/entity2.getMass());

        entity2.setVelocity(entity2.getVelocity().add(acceleration));
    }

    /**
     * Rotates a given point (velocity vector) by some angle
     * @param velocity a vector to be rotated
     * @param angle    angle in radians by which the point is rotated
     * @return a rotated Point2D object
     */
    public static Point2D rotate(Point2D velocity, double angle) {
        double x = velocity.getX() * Math.cos(angle) - velocity.getY() * Math.sin(angle);
        double y = velocity.getX() * Math.sin(angle) + velocity.getY() * Math.cos(angle);

        return new Point2D(x, y);
    }
}

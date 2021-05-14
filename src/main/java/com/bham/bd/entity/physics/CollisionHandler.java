package com.bham.bd.entity.physics;

import com.bham.bd.entity.MovingEntity;
import com.bham.bd.utils.GeometryEnhanced;
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
     *
     * @see <a href="https://simple.wikipedia.org/wiki/Elastic_collision">wikipedia.org/wiki/Elastic_collision</a>
     */
    public static void resolveElasticCollision(MovingEntity entity1, MovingEntity entity2) {
        Point2D deltaVelocity = entity1.getVelocity().subtract(entity2.getVelocity());
        Point2D deltaDistance = entity2.getCenterPosition().subtract(entity1.getCenterPosition());

        if((deltaVelocity.dotProduct(deltaDistance) >= 0)) {
            double angle = -Math.atan2(deltaDistance.getY(), deltaDistance.getX());

            double mass1 = entity1.getMass();
            double mass2 = entity1.getMass();

            Point2D u1 = GeometryEnhanced.rotate(entity1.getVelocity(), angle);
            Point2D u2 = GeometryEnhanced.rotate(entity2.getVelocity(), angle);

            Point2D v1 = new Point2D(u1.getX() * (mass1 - mass2) / (mass1 + mass2) + u2.getX() * 2 * mass2 / (mass1 + mass2), u1.getY());
            Point2D v2 = new Point2D(u2.getX() * (mass1 - mass2) / (mass1 + mass2) + u1.getX() * 2 * mass1 / (mass1 + mass2), u2.getY());

            v1 = GeometryEnhanced.rotate(v1, -angle);
            v2 = GeometryEnhanced.rotate(v2, -angle);

            entity1.setVelocity(v1);
            entity2.setVelocity(v2);
        }
    }

    /**
     * Performs continuous collision modification
     *
     * <p>This method checks for the direction from the first entity's center position to the second one and adds acceleration
     * to either of the entities depending on the situation <b>while both are intercepting</b>:</p>
     * <ul>
     *     <li>If the first entity is moving towards the second entity (or at least some part of its velocity), it pushes the
     *     second entity away from its center by appending acceleration to that entity's velocity.</li>
     *     <li>If the first entity is moving away the second entity (or at least some part of its velocity) or is standing still,
     *     it pushes itself from that entity's center by appending acceleration to itself's velocity.</li>
     * </ul>
     * <p>This is useful when an impulse has ended but entities are still colliding.</p>
     *
     * @param entity1 1st entity colliding
     * @param entity2 2nd entity colliding
     */
    public static void resolveContinuousCollision(MovingEntity entity1, MovingEntity entity2) {
        Point2D deltaDistance = entity2.getCenterPosition().subtract(entity1.getCenterPosition());
        Point2D force = deltaDistance.normalize().multiply(-3 * Steering.FRICTION);

        if(entity1.getVelocity().magnitude() == 0 || entity1.getVelocity().dotProduct(force) < 0) {
            Point2D acceleration = force.multiply(-1/entity1.getMass());
            entity1.setVelocity(entity1.getVelocity().add(acceleration));
        } else {
            Point2D acceleration = force.multiply(1/entity2.getMass());
            entity2.setVelocity(entity2.getVelocity().add(acceleration));
        }
    }
}

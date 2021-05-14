package com.bham.bd.entity.physics;

import com.bham.bd.components.characters.Player;
import com.bham.bd.entity.MovingEntity;
import com.bham.bd.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.bham.bd.utils.GeometryEnhanced.*;

/**
 * class to calculate steering force for game characters
 */
public class Steering {
    private MovingEntity agent;
    private Point2D steeringForce = new Point2D(0,0);
    private Point2D target = new Point2D(0,0);
    private double seekWeight = 1.0;

    public static final double FRICTION = -1;

    /**
     * switches for behaviors
     */
    private boolean keysOn = false;
    private boolean decelerateOn = false;
    private boolean seekOn = false;

    public void setKeys(boolean val) {
        keysOn = val;
    }
    public void setDecelerate(boolean val) {
        decelerateOn = val;
    }
    public void seekOn() {
        seekOn = true;
    }
    public void seekOff() {
        seekOn = false;
    }

    /**
     * Set the target an agent wants to seek
     * @param t the target position
     */
    public void setTarget(Point2D t) {
        target = t;
    }

    /**
     * This function ensures that the new force to add is within the agent's tolerance range
     * @param curForce the current force during accumulation
     * @param newForce the new force going to be added to the curForce
     * @return truncate version of newForce, zero if no capcity left
     */
    private Point2D truncateIfOverweight(Point2D curForce, Point2D newForce) {
        
        double MagnitudeSoFar = curForce.magnitude();

        //calculate how much steering force remains to be used by this vehicle
        double MagnitudeRemaining = agent.getMaxForce() - MagnitudeSoFar;
        
        //return false if there is no more force left to use
        if (MagnitudeRemaining <= 1E-16) {
            return new Point2D(0,0);
        }

        //calculate the magnitude of the force we want to add
        double MagnitudeToAdd = newForce.magnitude();

        //if the magnitude of the sum of ForceToAdd and the running total
        //does not exceed the maximum force available to this vehicle, just
        //add together. Otherwise add as much of the ForceToAdd vector is
        //possible without going over the max.
        if (MagnitudeToAdd < MagnitudeRemaining) {
            return newForce;
        } else {
            MagnitudeToAdd = MagnitudeRemaining;
            return newForce.normalize().multiply(MagnitudeToAdd);
        }
    }

    /**
     * Validates acceleration/deceleration value
     *
     * <p>Checks if acceleration exceeds the bounds of a preferred min/max speed and returns a new delta velocity
     * vector which will ensure, once it is added to the current speed, it won't exceed the boundary.</p>
     *
     * <p><b>Note:</b> only works when force is parallel to velocity, i.e., we can't check for sideways
     * acceleration/deceleration.</p>
     *
     * @param deltaVelocity velocity change we would like to apply to agent's current velocity
     * @param isForward     true if we want to accelerate and false if we want to decelerate
     * @return Point2D object representing a velocity change vector needed to accelerate/decelerate to a desired max/min speed (or 0 if that speed is reached)
     */
    public Point2D validateAcceleration(Point2D deltaVelocity, boolean isForward) {
        double minSpeed = 1E-16;
        double maxSpeed = agent.getMaxSpeed();
        double nowSpeed = agent.getVelocity().magnitude();

        if(isForward) {
            if(nowSpeed >= maxSpeed) {
                //System.out.println("Can't accelerate more: max speed reached");
                return new Point2D(0, 0);
            } else if(nowSpeed + deltaVelocity.magnitude() > maxSpeed) {
                //System.out.println("Only accelerating a bit: max speed almost reached");
                return agent.getVelocity().normalize().multiply(maxSpeed).subtract(agent.getVelocity());
            }
        } else {
            if(nowSpeed <= minSpeed) {
                //System.out.println("Can't decelerate more: min speed reached");
                return new Point2D(0, 0);
            } else if(nowSpeed - deltaVelocity.magnitude() < 0) {
                //System.out.println("Only decelerating a bit: min speed almost reached");
                return agent.getVelocity().multiply(-1);
            }
        }

        //System.out.println("Fully accelerating / decelerating: " + deltaVelocity);
        return deltaVelocity;
    }

    /**
     * Validates force perpendicular to velocity
     *
     * <p>This method takes force and returns adjusted force which can be applied to agent to increase acceleration/
     * deceleration without exceeding the min/max speed bounds.</p>
     *
     * @param force     force to be applied to agent
     * @param isForward true if the force is in the same direction as velocity vector and false otherwise
     * @return Point2D object representing a force vector needed to accelerate/decelerate to a desired max/min speed (or 0 if that speed is reached)
     *
     * @see Steering#validateAcceleration(Point2D, boolean)
     */
    public Point2D validateForce(Point2D force, boolean isForward) {
        Point2D acceleration = force.multiply(1/agent.getMass());
        Point2D validAcceleration = validateAcceleration(acceleration, isForward);

        return validAcceleration.multiply(agent.getMass());
    }

    /* .......................................................

     BEGIN BEHAVIOR DECLARATIONS

     .......................................................*/

    /**
     * Calculates force based on whether any key is pressed
     *
     * <p>If we have some keys pressed, it means we must have a positive force applied to the direction of the <i>velocity</i>
     * vector (or heading if velocity is 0). Otherwise, if no keys are pressed, we need to apply a negative force to the
     * opposite direction of <i>velocity</i>. We also want to apply a bigger force for deceleration to stop faster.</p>
     *
     * <p><b>Note:</b> we cannot check for the direction of the <i>heading</i> param because, if a player bumps to something,
     * their velocity vector may change but heading would remain the same. So we always must apply force to the direction of
     * <i>velocity</i> param.</p>
     *
     * @return positive or negative force to be applied player's velocity
     */
    public Point2D keysForce() {
        if(agent instanceof Player) {
            double force = -FRICTION;
            boolean isForward = ((Player) agent).getNumDirKeysPressed() > 0;

            // If velocity is 0, we can only use a positive force as we will never want to go back
            // If the player was going back and its velocity ended at 0, it will be handled by other methods
            // Otherwise its the usual case - we add force to the same or the opposite direction of velocity
            if (isZero(agent.getVelocity())) {
                return agent.getHeading().multiply(force);
            } else {
                return agent.getVelocity().normalize().multiply(isForward ? force : -2.5 * force);
            }
        }
        return new Point2D(0, 0);
    }

    /**
     * Calculates the force to approach a target
     * @param target the target position to seek
     * @return a force to push the entity to an target
     */
    public Point2D seek(Point2D target) {

        Point2D DesiredVelocity = target
                                    .subtract(agent.getCenterPosition())
                                    .normalize()
                                    .multiply(agent.getMaxSpeed());

        return DesiredVelocity.subtract(agent.getVelocity());
    }
    
    /* .......................................................

     END BEHAVIOR DECLARATIONS

     .......................................................*/


    public Steering(MovingEntity agent) {
        this.agent = agent;
    }

    /**
     * calculates the accumulated steering force by Weighted Truncated Running Sum with Prioritization
     * @return the force to affect entity's movement
     */
    public Point2D calculate() {
        steeringForce = new Point2D(0,0);
        steeringForce = calculatePrioritized();
        return steeringForce;
    }

    private Point2D calculatePrioritized() {
        Point2D force = new Point2D(0,0);

        if(keysOn) {
            force = keysForce();

            if(GeometryEnhanced.isZero(force)) {
                return steeringForce;
            } else {
                steeringForce = steeringForce.add(force);
            }
        }

        if (seekOn) {
//            System.out.println("seekOn");
            force = seek(target).multiply(seekWeight);
            Point2D temp = truncateIfOverweight(steeringForce, force);
            if (GeometryEnhanced.isZero(temp)) {
                return steeringForce;
            } else {
                steeringForce = steeringForce.add(temp);
            }
        }

        if(decelerateOn) {
            double scalarForce = -1;
            force = agent.getVelocity().normalize().multiply(scalarForce);
            Point2D temp = validateForce(force, false);

            if (GeometryEnhanced.isZero(temp)) {
                return steeringForce;
            } else {
                steeringForce = steeringForce.add(temp);
            }
        }
        return steeringForce;
    }

    public void render(GraphicsContext gc){
        double x =agent.getPosition().getX();
        double y = agent.getPosition().getY();
        Point2D velocity = agent.getVelocity();

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x + velocity.getX() * 10, y + velocity.getY() * 10);

//        gc.setStroke(Color.WHITE);
//        gc.setLineWidth(2.0);
//
//        gc.strokeLine(x, y, x + acceleration.getX() * 10, x + acceleration.getY() * 10);

        gc.setFill(Color.WHITE);
        gc.fillRoundRect(target.getX(),target.getY(),4,4,1,1);
    }
}

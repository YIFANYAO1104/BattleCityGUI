package com.bham.bc.components.characters;

import com.bham.bc.components.BackendServices;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;

import static com.bham.bc.utils.GeometryEnhanced.*;

/**
 * Desc: class to encapsulate steering behaviors for a GameCharacter
 */
public class Steering {
//--------------------------- Constants ----------------------------------

    /**
     * the radius of the constraining circle for the wander behavior
     */
    public static final double WanderRad = 1.2;
    /**
     * distance the wander circle is projected in front of the agent
     */
    public static final double WanderDist = 4.0;
    /**
     * the maximum amount of displacement along the circle each frame
     */
    public static final double WanderJitterPerSec = 40.0;
//------------------------------------------------------------------------

    /**
     * a pointer to the owner of this instance
     */
    private GameCharacter agent;
    /**
     * pointer to the world data
     */
    private BackendServices world;
    /**
     * the steering force created by the combined effect of all the selected
     * behaviors
     */
    private Point2D steeringForce = new Point2D(0,0);
    /**
     * the current target for seek
     */
    private Point2D target = new Point2D(0,0);
    /**
     * explained above
     */
    private double m_dWanderJitter;
    private double m_dWanderRadius;
    private double m_dWanderDistance;

    private double m_dWeightSeparation = 10.0;
    private double m_dWeightWander = 1.0;
    private double m_dWeightWallAvoidance = 10.0;
    private double m_dWeightSeek = 0.5;
    private double m_dWeightArrive = 1.0;

    public static final double FRICTION = -1;

    public void setTarget(Point2D t) {
        target = t;
    }

    /**
     * This function calculates how much of its max steering force the vehicle
     * has left to apply and then applies that amount of the force to add.
     */
    private Point2D accumulateForce(Point2D RunningTot, Point2D ForceToAdd) {
//        ForceToAdd = new Point2D(ForceToAdd); // work with copy
        //calculate how much steering force the vehicle has used so far
        double MagnitudeSoFar = RunningTot.magnitude();

        //calculate how much steering force remains to be used by this vehicle
        double MagnitudeRemaining = agent.getMaxForce() - MagnitudeSoFar;
//        System.out.println("So far: " + MagnitudeSoFar);
//        System.out.println("Rem: " + MagnitudeRemaining);

        //return false if there is no more force left to use
        if (MagnitudeRemaining <= 1E-8) {
            return new Point2D(0,0);
        }

        //calculate the magnitude of the force we want to add
        double MagnitudeToAdd = ForceToAdd.magnitude();

        //if the magnitude of the sum of ForceToAdd and the running total
        //does not exceed the maximum force available to this vehicle, just
        //add together. Otherwise add as much of the ForceToAdd vector is
        //possible without going over the max.
        if (MagnitudeToAdd < MagnitudeRemaining) {
//            RunningTot = RunningTot.add(ForceToAdd);
            return ForceToAdd;
        } else {
            MagnitudeToAdd = MagnitudeRemaining;

            //add it to the steering force
            //mul(Vec2DNormalize(ForceToAdd), MagnitudeToAdd)
//            RunningTot = RunningTot.add(ForceToAdd.normalize().multiply(MagnitudeToAdd));
            return ForceToAdd.normalize().multiply(MagnitudeToAdd);
        }


//        return RunningTot;
    }

    /**
     * Validates acceleration/deceleration value
     *
     * <p>Checks if acceleration exceeds the bounds of a preferred min/max speed and returns a new delta velocity
     * vector which will ensure, once it is added to the current speed, it won't exceed the boundary.</p>
     *
     * <p><b>Note: </b> only works when force is perpendicular to velocity, i.e., we can't check for sideways
     * acceleration/deceleration.</p>
     *
     * @param deltaVelocity velocity change we would like to apply to agent's current velocity
     * @param isForward     true if we want to accelerate and false if we want to decelerate
     * @return Point2D object representing a velocity change vector needed to accelerate/decelerate to a desired max/min speed (or 0 if that speed is reached)
     */
    public Point2D validateAcceleration(Point2D deltaVelocity, boolean isForward) {
        double minSpeed = 1E-8;
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
     * Given a target, this behavior returns a steering force which will direct
     * the agent towards the target
     */
    public Point2D seek(final Point2D target) {

        Point2D DesiredVelocity = target
                                    .subtract(agent.getCenterPosition())
                                    .normalize()
                                    .multiply(agent.getMaxSpeed());
//        System.out.println("DesiredVelocity = "+DesiredVelocity);

        return DesiredVelocity.subtract(agent.getVelocity());
    }

    /**
     * this behavior is similar to seek but it attempts to arrive at the target
     * with a zero velocity
     */
    public Point2D arrive(final Point2D target) {
        Point2D ToTarget = target.subtract(agent.getCenterPosition());

        //calculate the distance to the target
        double dist = ToTarget.magnitude();

        if (dist > 0) {
            //calculate the speed required to reach the target given the desired deceleration
            double speed = dist /0.3;//the bigger

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, agent.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize
            //the ToTarget vector because we have already gone to the trouble
            //of calculating its length: dist.
            Point2D DesiredVelocity = ToTarget.multiply(speed).multiply(1./dist);

            return DesiredVelocity.subtract(agent.getVelocity());
        }

        return new Point2D(0, 0);
    }

    public Point2D arrive_improved(final Point2D target) {
        Point2D ToTarget = target.subtract(agent.getCenterPosition());

        //calculate the distance to the target
        double dist = ToTarget.magnitude();

        if (dist > 0) {
            double radius = 10;
            Point2D DesiredVelocity = ToTarget.multiply(agent.getMaxSpeed()/radius);

            return DesiredVelocity.subtract(agent.getVelocity());
        }

        return new Point2D(0, 0);
    }

    /**
     * This behavior makes the agent wander about randomly
     */
//    private Point2D Wander() {
//        //first, add a small random vector to the target's position
//        m_vWanderTarget.add(new Point2D(RandomClamped() * m_dWanderJitter,
//                RandomClamped() * m_dWanderJitter));
//
//        //reproject this new vector back on to a unit circle
//        m_vWanderTarget.normalize();
//
//        //increase the length of the vector to the same as the radius
//        //of the wander circle
//        m_vWanderTarget.multiply(m_dWanderRadius);
//
//        //move the target into a position WanderDist in front of the agent
//        Point2D target = m_vWanderTarget.add(new Point2D(m_dWanderDistance, 0));
//
//        //project the target into world space
//        Point2D Target = PointToWorldSpace(target,
//                m_pRaven_Bot.getHeading(),
//                m_pRaven_Bot.Side(),
//                m_pRaven_Bot.getPosition());
//
//        //and steer towards it
//        return Target.subtract(m_pRaven_Bot.getPosition());
//    }
    public Point2D wander_improved() {
        Point2D circleCenter = agent.getCenterPosition()
                .add(agent.getHeading()
                        .normalize()
                        .multiply(m_dWanderDistance));

        Point2D target = circleCenter
                .add(
                        rotate(new Point2D(0,0),
                                new Point2D(m_dWanderRadius,0),
                                GeometryEnhanced.randDouble(0,360))
                );

        //and steer towards it
        return seek(target);
    }
    /* .......................................................

     END BEHAVIOR DECLARATIONS

     .......................................................*/


    public Steering(GameCharacter agent) {

//        m_pWorld = world;
        this.agent = agent;

        m_dWanderDistance = WanderDist;
        m_dWanderJitter = WanderJitterPerSec;
        m_dWanderRadius = WanderRad;

        //stuff for the wander behavior
//        double theta = rand.nextDouble() * Math.PI * 2;
    }

    //---------------------------------dtor ----------------------------------

    /**
     * calculates the accumulated steering force according to the method set in
     * m_SummingMethod
     */
    public Point2D calculate() {
        //reset the steering force
        steeringForce = new Point2D(0,0);

        //tag neighbors if any of the following 3 group behaviors are switched on
//        if (On(separation)) {
//            m_pWorld.TagRaven_BotsWithinViewRange(m_pRaven_Bot, m_dViewDistance);
//        }

        steeringForce = calculatePrioritized();

        return steeringForce;
    }

    private Point2D calculatePrioritized() {
        Point2D force = new Point2D(0,0);

//        if (On(wall_avoidance)) {
//            force = mul(WallAvoidance(m_pWorld.GetMap().GetWalls()),
//                    m_dWeightWallAvoidance);
//
//            if (!AccumulateForce(m_vSteeringForce, force)) {
//                return m_vSteeringForce;
//            }
//        }


        //these next three can be combined for flocking behavior (wander is
        //also a good behavior to add into this mix)

//        if (On(separation)) {
//            force = mul(Separation(m_pWorld.GetAllBots()), m_dWeightSeparation);
//
//            if (!AccumulateForce(m_vSteeringForce, force)) {
//                return m_vSteeringForce;
//            }
//        }

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
            force = seek(target).multiply(m_dWeightSeek);
            Point2D temp = accumulateForce(steeringForce, force);
            if (GeometryEnhanced.isZero(temp)) {
                return steeringForce;
            } else {
                steeringForce = steeringForce.add(temp);
            }
        }

        if (arriveOn) {
//            System.out.println("arriveOn");
            force = arrive_improved(target).multiply(m_dWeightArrive);

            Point2D temp = accumulateForce(steeringForce, force);
            if (GeometryEnhanced.isZero(temp)) {
                return steeringForce;
            } else {
                steeringForce = steeringForce.add(temp);
            }
        }

        if (wanderOn) {
            force = wander_improved().multiply(m_dWeightWander);

            Point2D temp = accumulateForce(steeringForce, force);
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

    private boolean keysOn = false;
    private boolean decelerateOn = false;
    private boolean seekOn = false;
    private boolean arriveOn = false;
    private boolean wanderOn = false;

    public void setKeysOn(boolean val) {
        keysOn = val;
    }

    public void setDecelerateOn(boolean val) {
        decelerateOn = val;
    }

    public void seekOn() {
        seekOn = true;
    }

    public void arriveOn() {
        arriveOn = true;
    }

    public void wanderOn() {
        wanderOn = true;
    }

//    public void separationOn() {
//        m_iFlags |= separation.b;
//    }
//
//    public void wallAvoidanceOn() {
//        m_iFlags |= wall_avoidance.b;
//    }

    public void seekOff() {
        seekOn = false;
    }

    public void arriveOff() {
        arriveOn = false;
    }

    public void wanderOff() {
        wanderOn = false;
    }

    public double WanderJitter() {
        return m_dWanderJitter;
    }

    public double WanderDistance() {
        return m_dWanderDistance;
    }

    public double WanderRadius() {
        return m_dWanderRadius;
    }

}

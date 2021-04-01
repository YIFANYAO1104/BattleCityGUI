package com.bham.bc.components.characters;

import com.bham.bc.components.BackendServices;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.bham.bc.utils.GeometryEnhanced.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Desc: class to encapsulate steering behaviors for a GameCharacter
 */
public class Steering {
//--------------------------- Constants ----------------------------------

    static private Random rand = new Random();

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
    private GameCharacter m_pRaven_Bot;
    /**
     * pointer to the world data
     */
    private BackendServices m_pWorld;
    /**
     * the steering force created by the combined effect of all the selected
     * behaviors
     */
    private Point2D m_vSteeringForce = new Point2D(0,0);
    //these can be used to keep track of friends, pursuers, or prey
    private GameCharacter m_pTargetAgent1;
    private GameCharacter m_pTargetAgent2;
    /**
     * the current target
     */
    private Point2D m_vTarget = new Point2D(0,0);
    /**
     * the current position on the wander circle the agent is attempting to
     * steer towards
     */
    private Point2D m_vWanderTarget;
    /**
     * explained above
     */
    private double m_dWanderJitter;
    private double m_dWanderRadius;
    private double m_dWanderDistance;






    /**
     * This function calculates how much of its max steering force the vehicle
     * has left to apply and then applies that amount of the force to add.
     */
    private boolean AccumulateForce(Point2D RunningTot,
                                    Point2D ForceToAdd) {
//        ForceToAdd = new Point2D(ForceToAdd); // work with copy
        //calculate how much steering force the vehicle has used so far
        double MagnitudeSoFar = RunningTot.magnitude();

        //calculate how much steering force remains to be used by this vehicle
        double MagnitudeRemaining = m_pRaven_Bot.getMaxForce() - MagnitudeSoFar;

        //return false if there is no more force left to use
        if (MagnitudeRemaining <= 0.0) {
            return false;
        }

        //calculate the magnitude of the force we want to add
        double MagnitudeToAdd = ForceToAdd.magnitude();

        //if the magnitude of the sum of ForceToAdd and the running total
        //does not exceed the maximum force available to this vehicle, just
        //add together. Otherwise add as much of the ForceToAdd vector is
        //possible without going over the max.
        if (MagnitudeToAdd < MagnitudeRemaining) {
            RunningTot.add(ForceToAdd);
        } else {
            MagnitudeToAdd = MagnitudeRemaining;

            //add it to the steering force
            //mul(Vec2DNormalize(ForceToAdd), MagnitudeToAdd)
            RunningTot.add(ForceToAdd.normalize().multiply(MagnitudeToAdd));
        }


        return true;
    }



    /* .......................................................

     BEGIN BEHAVIOR DECLARATIONS

     .......................................................*/
    /**
     * Given a target, this behavior returns a steering force which will direct
     * the agent towards the target
     */
    public Point2D seek(final Point2D target) {

        Point2D DesiredVelocity = target
                                    .subtract(m_pRaven_Bot.getCenterPosition())
                                    .normalize()
                                    .multiply(m_pRaven_Bot.getMaxSpeed());
        System.out.println("DesiredVelocity = "+DesiredVelocity);

        return DesiredVelocity.subtract(m_pRaven_Bot.getVelocity());
    }

    /**
     * this behavior is similar to seek but it attempts to arrive at the target
     * with a zero velocity
     */
    public Point2D arrive(final Point2D target) {
        Point2D ToTarget = target.subtract(m_pRaven_Bot.getCenterPosition());

        //calculate the distance to the target
        double dist = ToTarget.magnitude();

        if (dist > 0) {
            //calculate the speed required to reach the target given the desired deceleration
            double speed = dist / (0.6);//the bigger

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, m_pRaven_Bot.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize
            //the ToTarget vector because we have already gone to the trouble
            //of calculating its length: dist.
            Point2D DesiredVelocity = ToTarget.multiply(speed).multiply(1./dist);

            return DesiredVelocity.subtract(m_pRaven_Bot.getVelocity());
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
        Point2D circleCenter = m_pRaven_Bot.getCenterPosition()
                .add(m_pRaven_Bot.getHeading()
                        .normalize()
                        .multiply(m_dWanderDistance));

        Point2D target = circleCenter
                .add(
                        rotate(new Point2D(0,0),
                                new Point2D(m_dWanderRadius,0),
                                randDouble(0,360))
                );

        //and steer towards it
        return seek(target);
    }

    private double randDouble(double rangeMin, double rangeMax){
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }

//    private Point2D rotate(Point2D center, Point2D p, double angleInDegrees){
//        Rotate r = new Rotate();
//        r.setPivotX(center.getX());
//        r.setPivotY(center.getY());
//        r.setAngle(angleInDegrees);
//        return r.transform(p);
//    }



    /* .......................................................

     END BEHAVIOR DECLARATIONS

     .......................................................*/


    public Steering(GameCharacter agent) {

//        m_pWorld = world;
        m_pRaven_Bot = agent;

        m_dWanderDistance = WanderDist;
        m_dWanderJitter = WanderJitterPerSec;
        m_dWanderRadius = WanderRad;

        //stuff for the wander behavior
        double theta = rand.nextDouble() * Math.PI * 2;

        //create a vector to a target position on the wander circle
        m_vWanderTarget = new Point2D(m_dWanderRadius * cos(theta),
                m_dWanderRadius * sin(theta));

    }

    //---------------------------------dtor ----------------------------------

    /**
     * calculates the accumulated steering force according to the method set in
     * m_SummingMethod
     */
//    public Point2D Calculate() {
//        //reset the steering force
//        m_vSteeringForce = new Point2D(0,0);
//
//        m_vSteeringForce = Wander();
//
//        return m_vSteeringForce;
//    }



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

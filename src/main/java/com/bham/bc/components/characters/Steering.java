package com.bham.bc.components.characters;

import com.bham.bc.components.BackendServices;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
    public static final double WanderDist = 2.0;
    /**
     * the maximum amount of displacement along the circle each frame
     */
    public static final double WanderJitterPerSec = 40.0;
//------------------------------------------------------------------------

    public static enum summing_method {

        weighted_average, prioritized, dithered
    };

    protected enum behavior_type {

        none(0x00000),
        seek(0x00002),
        arrive(0x00008),
        wander(0x00010),
        separation(0x00040),
        wall_avoidance(0x00200);
        public final int b;

        private behavior_type(int b) {
            this.b = b;
        }
    };
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
     * a vertex buffer to contain the feelers rqd for wall avoidance
     */
    private List<Point2D> m_Feelers = new ArrayList<Point2D>(3);
    /**
     * the length of the 'feeler/s' used in wall detection
     */
    private double m_dWallDetectionFeelerLength;
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
     * multipliers. These can be adjusted to effect strength of the appropriate
     * behavior.
     */
    private double m_dWeightSeparation;
    private double m_dWeightWander;
    private double m_dWeightWallAvoidance;
    private double m_dWeightSeek;
    private double m_dWeightArrive;
    /**
     * how far the agent can 'see'
     */
    private double m_dViewDistance;
    /**
     * binary flags to indicate whether or not a behavior should be active
     */
    private int m_iFlags;

    /**
     * Arrive makes use of these to determine how quickly a GameCharacter should
     * decelerate to its target
     */
    private enum Deceleration {

        slow(3), normal(2), fast(1);
        public final int num;

        private Deceleration(int num) {
            this.num = num;
        }
    };
    /**
     * default
     */
    private Deceleration m_Deceleration;
    /**
     * is cell space partitioning to be used or not?
     */
    private boolean m_bCellSpaceOn;
    /**
     * what type of method is used to sum any active behavior
     */
    private summing_method m_SummingMethod;

    /**
     * this function tests if a specific bit of m_iFlags is set
     */
    private boolean On(behavior_type bt) {
        return (m_iFlags & bt.b) == bt.b;
    }

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

    /**
     * creates the antenna utilized by the wall avoidance behavior
     */
    private void CreateFeelers() {
        m_Feelers.clear();
        //feeler pointing straight in front

        Point2D idk = m_pRaven_Bot.getHeading()
                .multiply(m_pRaven_Bot.getSpeed())
                .multiply(m_dWallDetectionFeelerLength)
                .add(m_pRaven_Bot.getPosition());

        m_Feelers.add(idk);

        //feeler to left
        Point2D temp = m_pRaven_Bot.getHeading();
        Vec2DRotateAroundOrigin(temp, HalfPi * 3.5);
        m_Feelers.add(add(m_pRaven_Bot.getPosition(), mul(m_dWallDetectionFeelerLength / 2.0, temp)));

        //feeler to right
        temp = m_pRaven_Bot.getHeading();
        Vec2DRotateAroundOrigin(temp, HalfPi * 0.5);
        m_Feelers.add(add(m_pRaven_Bot.getPosition(), mul(m_dWallDetectionFeelerLength / 2.0, temp)));
    }

    /* .......................................................

     BEGIN BEHAVIOR DECLARATIONS

     .......................................................*/
    /**
     * Given a target, this behavior returns a steering force which will direct
     * the agent towards the target
     */
    private Point2D Seek(final Point2D target) {

        target.subtract(m_pRaven_Bot.getPosition()).normalize().multiply(m_pRaven_Bot.getMaxSpeed());

//        Point2D DesiredVelocity = mul(Vec2DNormalize(sub(target, m_pRaven_Bot.getPosition())),
//                m_pRaven_Bot.getMaxSpeed());

        Point2D DesiredVelocity = target.subtract(m_pRaven_Bot.getPosition())
                .normalize()
                .multiply(m_pRaven_Bot.getMaxSpeed());

//        return sub(DesiredVelocity, m_pRaven_Bot.getVelocity());
        return DesiredVelocity.subtract(m_pRaven_Bot.getVelocity());
    }

    /**
     * this behavior is similar to seek but it attempts to arrive at the target
     * with a zero velocity
     */
    private Point2D Arrive(final Point2D target, final Deceleration deceleration) {
//        Point2D ToTarget = sub(target, m_pRaven_Bot.getPosition());
        Point2D ToTarget = target.subtract(m_pRaven_Bot.getPosition());

        //calculate the distance to the target
        double dist = ToTarget.magnitude();

        if (dist > 0) {
            //because Deceleration is enumerated as an int, this value is required
            //to provide fine tweaking of the deceleration..
            final double DecelerationTweaker = 0.3;

            //calculate the speed required to reach the target given the desired
            //deceleration
            double speed = dist / ((double) deceleration.num * DecelerationTweaker);

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, m_pRaven_Bot.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize 
            //the ToTarget vector because we have already gone to the trouble
            //of calculating its length: dist. 
//            Point2D DesiredVelocity = div(mul(ToTarget, speed), dist);
            Point2D DesiredVelocity = ToTarget.multiply(speed)
                                                .multiply(1./dist);

//            return sub(DesiredVelocity, m_pRaven_Bot.getVelocity());
            return DesiredVelocity.subtract(m_pRaven_Bot.getVelocity());
        }

        return new Point2D(0, 0);
    }

    /**
     * This behavior makes the agent wander about randomly
     */
    private Point2D Wander() {
        //first, add a small random vector to the target's position
        m_vWanderTarget.add(new Point2D(RandomClamped() * m_dWanderJitter,
                RandomClamped() * m_dWanderJitter));

        //reproject this new vector back on to a unit circle
        m_vWanderTarget.normalize();

        //increase the length of the vector to the same as the radius
        //of the wander circle
        m_vWanderTarget.multiply(m_dWanderRadius);

        //move the target into a position WanderDist in front of the agent
        Point2D target = m_vWanderTarget.add(new Point2D(m_dWanderDistance, 0));

        //project the target into world space
        Point2D Target = PointToWorldSpace(target,
                m_pRaven_Bot.getHeading(),
                m_pRaven_Bot.Side(),
                m_pRaven_Bot.getPosition());

        //and steer towards it
        return Target.subtract(m_pRaven_Bot.getPosition());
    }

    /**
     * This returns a steering force that will keep the agent away from any
     * walls it may encounter
     */
    private Point2D WallAvoidance(final List<Wall2D> walls) {
        //the feelers are contained in a std::vector, m_Feelers
        CreateFeelers();

        double DistToThisIP = 0.0;
        double DistToClosestIP = MaxDouble;

        //this will hold an index into the vector of walls
        int ClosestWall = -1;

        Point2D SteeringForce = new Point2D(0,0),
                point = new Point2D(0,0), //used for storing temporary info
                ClosestPoint = new Point2D(0,0);  //holds the closest intersection point

        //examine each feeler in turn
        for (int flr = 0; flr < m_Feelers.size(); ++flr) {
            //run through each wall checking for any intersection points
            for (int w = 0; w < walls.size(); ++w) {
                DoubleRef ref = new DoubleRef(DistToThisIP);
                boolean intersection = LineIntersection2D(m_pRaven_Bot.getPosition(),
                        m_Feelers.get(flr),
                        walls.get(w).From(),
                        walls.get(w).To(),
                        ref,
                        point);
                DistToThisIP = ref.get();
                if (intersection) {
                    //is this the closest found so far? If so keep a record
                    if (DistToThisIP < DistToClosestIP) {
                        DistToClosestIP = DistToThisIP;

                        ClosestWall = w;

                        ClosestPoint = point;
                    }
                }
            }//next wall


            //if an intersection point has been detected, calculate a force  
            //that will direct the agent away
            if (ClosestWall >= 0) {
                //calculate by what distance the projected position of the agent
                //will overshoot the wall
                Point2D OverShoot = m_Feelers.get(flr).subtract(ClosestPoint);

                //create a force in the direction of the wall normal, with a 
                //magnitude of the overshoot
                SteeringForce = mul(walls.get(ClosestWall).Normal(), OverShoot.Length());
            }

        }//next feeler

        return SteeringForce;
    }

    /**
     * this calculates a force repelling from the other neighbors
     */
    private Point2D Separation(final List<GameCharacter> neighbors) {
        //iterate through all the neighbors and calculate the vector from the
        Point2D SteeringForce = new Point2D(0,0);

        Iterator<GameCharacter> iterator = neighbors.iterator();
        while (iterator.hasNext()) {
            GameCharacter it = iterator.next();
            //make sure this agent isn't included in the calculations and that
            //the agent being examined is close enough. ***also make sure it doesn't
            //include the evade target ***
            if ((it != m_pRaven_Bot) && it.IsTagged()
                    && (it != m_pTargetAgent1)) {
                Point2D ToAgent = m_pRaven_Bot.getPosition().subtract(it.getPosition());

                //scale the force inversely proportional to the agents distance  
                //from its neighbor.
                SteeringForce.add(ToAgent.normalize().multiply(1./ToAgent.magnitude()));
            }
        }

        return SteeringForce;
    }

    /* .......................................................

     END BEHAVIOR DECLARATIONS

     .......................................................*/
    /**
     * this method calls each active steering behavior in order of priority and
     * acumulates their forces until the max steering force magnitude is
     * reached, at which time the function returns the steering force
     * accumulated to that point
     */
    private Point2D CalculatePrioritized() {
        Point2D force;

        if (On(wall_avoidance)) {
            force = WallAvoidance(m_pWorld.GetMap().GetWalls()).multiply(m_dWeightWallAvoidance);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }


        //these next three can be combined for flocking behavior (wander is
        //also a good behavior to add into this mix)

        if (On(separation)) {
            force = Separation(m_pWorld.GetAllBots()).multiply(m_dWeightSeparation);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }


        if (On(seek)) {
            force = Seek(m_vTarget).multiply(m_dWeightSeek);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }


        if (On(arrive)) {
            force = Arrive(m_vTarget, m_Deceleration).multiply(m_dWeightArrive);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }

        if (On(wander)) {
            force = Wander().multiply(m_dWeightWander);

            if (!AccumulateForce(m_vSteeringForce, force)) {
                return m_vSteeringForce;
            }
        }


        return m_vSteeringForce;
    }

    public Steering(BackendServices world, GameCharacter agent) {

        m_pWorld = world;
        m_pRaven_Bot = agent;
        m_iFlags = 0;
        m_dWeightSeparation = script.GetDouble("SeparationWeight");
        m_dWeightWander = script.GetDouble("WanderWeight");
        m_dWeightWallAvoidance = script.GetDouble("WallAvoidanceWeight");
        m_dViewDistance = script.GetDouble("ViewDistance");
        m_dWallDetectionFeelerLength = script.GetDouble("WallDetectionFeelerLength");
        m_Feelers = new ArrayList<Point2D>(3);
        m_Deceleration = Deceleration.normal;
        m_pTargetAgent1 = null;
        m_pTargetAgent2 = null;
        m_dWanderDistance = WanderDist;
        m_dWanderJitter = WanderJitterPerSec;
        m_dWanderRadius = WanderRad;
        m_dWeightSeek = script.GetDouble("SeekWeight");
        m_dWeightArrive = script.GetDouble("ArriveWeight");
        m_bCellSpaceOn = false;
        m_SummingMethod = summing_method.prioritized;

        //stuff for the wander behavior
        double theta = RandFloat() * TwoPi;

        //create a vector to a target position on the wander circle
        m_vWanderTarget = new Point2D(m_dWanderRadius * cos(theta),
                m_dWanderRadius * sin(theta));

    }

    //---------------------------------dtor ----------------------------------

    /**
     * calculates the accumulated steering force according to the method set in
     * m_SummingMethod
     */
    public Point2D Calculate() {
        //reset the steering force
        m_vSteeringForce = new Point2D(0,0);

        //tag neighbors if any of the following 3 group behaviors are switched on
        if (On(separation)) {
            m_pWorld.TagRaven_BotsWithinViewRange(m_pRaven_Bot, m_dViewDistance);
        }

        m_vSteeringForce = CalculatePrioritized();

        return m_vSteeringForce;
    }

    /**
     * calculates the component of the steering force that is parallel with the
     * GameCharacter heading
     *
     * @return the forward oomponent of the steering force
     */
    public double ForwardComponent() {
        return m_pRaven_Bot.getHeading().dotProduct(m_vSteeringForce);
    }

    /**
     * calculates the component of the steering force that is perpendicuar with
     * the GameCharacter heading
     */
    public double SideComponent() {
        return m_pRaven_Bot.Side().Dot(m_vSteeringForce);
    }

    public void SetTarget(Point2D t) {
        m_vTarget = t;
    }

    public Point2D Target() {
        return m_vTarget;
    }

    public void SetTargetAgent1(GameCharacter Agent) {
        m_pTargetAgent1 = Agent;
    }

    public void SetTargetAgent2(GameCharacter Agent) {
        m_pTargetAgent2 = Agent;
    }

    public Point2D Force() {
        return m_vSteeringForce;
    }

    public void SetSummingMethod(summing_method sm) {
        m_SummingMethod = sm;
    }

    public void SeekOn() {
        m_iFlags |= seek.b;
    }

    public void ArriveOn() {
        m_iFlags |= arrive.b;
    }

    public void WanderOn() {
        m_iFlags |= wander.b;
    }

    public void SeparationOn() {
        m_iFlags |= separation.b;
    }

    public void WallAvoidanceOn() {
        m_iFlags |= wall_avoidance.b;
    }

    public void SeekOff() {
        if (On(seek)) {
            m_iFlags ^= seek.b;
        }
    }

    public void ArriveOff() {
        if (On(arrive)) {
            m_iFlags ^= arrive.b;
        }
    }

    public void WanderOff() {
        if (On(wander)) {
            m_iFlags ^= wander.b;
        }
    }

    public void SeparationOff() {
        if (On(separation)) {
            m_iFlags ^= separation.b;
        }
    }

    public void WallAvoidanceOff() {
        if (On(wall_avoidance)) {
            m_iFlags ^= wall_avoidance.b;
        }
    }

    public boolean SeekIsOn() {
        return On(seek);
    }

    public boolean ArriveIsOn() {
        return On(arrive);
    }

    public boolean WanderIsOn() {
        return On(wander);
    }

    public boolean SeparationIsOn() {
        return On(separation);
    }

    public boolean WallAvoidanceIsOn() {
        return On(wall_avoidance);
    }

    public List<Point2D> GetFeelers() {
        return m_Feelers;
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

    public double SeparationWeight() {
        return m_dWeightSeparation;
    }
}

package com.bham.bc.entity.ai.goals.atomic;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.PathEdge;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_traverse_edge;
import static com.bham.bc.entity.graph.edge.GraphEdge.normal;
import static com.bham.bc.entity.graph.edge.GraphEdge.shoot;

public class Goal_TraverseEdge extends Goal {

    /**
     * the edge the bot will follow
     */
    private PathEdge edge;
    /**
     * true if m_Edge is the last in the path.
     */
    private boolean isLast;



    //---------------------------- ctor -------------------------------------------
    //-----------------------------------------------------------------------------
    public Goal_TraverseEdge(GameCharacter gameCharacter,
                             PathEdge edge,
                             boolean isLast) {

        super(gameCharacter, goal_traverse_edge);
        this.edge = new PathEdge(edge);
        this.isLast = isLast;

    }

    //the usual suspects
    @Override
    public void activate() {
        status = active;

        //the edge behavior flag may specify a type of movement that necessitates a 
        //change in the bot's max possible speed as it follows this edge
        switch (edge.getBehavior()) {
            case shoot://TODO:ADD SHOOTING
                agent.getTargetingSystem().hitObsOn();
            break;
            case normal:break;
            default:
                throw new RuntimeException("<Goal_FollowPath::Activate>: Unrecognized edge type");
        }

        //set the steering target
        agent.getSteering().setTarget(edge.getDestination());

        //Set the appropriate steering behavior. If this is the last edge in the path
        //the bot should arrive at the position it points to, else it should seek
        if (isLast) {
//            agent.getSteering().arriveOn();
            agent.getSteering().seekOn();
        } else {
            agent.getSteering().seekOn();
        }
    }

    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        if (agent.isReached(edge.getDestination())) {
            status = completed;
        }

        return status;
    }

    @Override
    public void terminate() {
        //turn off steering behaviors.
        agent.getSteering().seekOff();
        agent.getTargetingSystem().hitObsOff();
//        agent.getSteering().arriveOff();

//        agent.setMaxSpeed(script.GetDouble("Bot_MaxSpeed"));
    }

    @Override
    public String toString() {
        String s = "";
        s += "TraverseEdge"+status;
        return s;
    }
}
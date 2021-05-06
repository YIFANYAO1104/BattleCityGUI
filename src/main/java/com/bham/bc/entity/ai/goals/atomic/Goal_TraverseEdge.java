package com.bham.bc.entity.ai.goals.atomic;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.PathEdge;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_traverse_edge;
import static com.bham.bc.entity.graph.edge.GraphEdge.normal;
import static com.bham.bc.entity.graph.edge.GraphEdge.shoot;

public class Goal_TraverseEdge extends Goal {

    /**
     * the edge that the agent is following
     */
    private PathEdge edge;

    /**
     * constructor
     * @param agent the owner of the goal
     * @param edge the edge that the agent is following
     */
    public Goal_TraverseEdge(GameCharacter agent, PathEdge edge) {

        super(agent, goal_traverse_edge);
        this.edge = new PathEdge(edge);
    }


    @Override
    public void activate() {
        status = active;
        switch (edge.getBehavior()) {
            case shoot:
                agent.getTargetingSystem().hitObsOn();
            break;
            case normal:break;
            default:
                throw new RuntimeException("behavior not supported");
        }
        agent.getSteering().setTarget(edge.getDestination());
        agent.getSteering().seekOn();
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
        agent.getSteering().seekOff();
        agent.getTargetingSystem().hitObsOff();
    }

    @Override
    public String toString() {
        String s = "";
        s += "TraverseEdge"+status;
        return s;
    }
}
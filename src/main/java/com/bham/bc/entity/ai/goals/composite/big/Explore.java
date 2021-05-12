package com.bham.bc.entity.ai.goals.composite.big;


import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.WaitForPath;
import com.bham.bc.entity.ai.goals.composite.CompositeGoal;
import com.bham.bc.entity.ai.goals.composite.helper.FollowPath;
import javafx.geometry.Point2D;

import static com.bham.bc.entity.ai.goals.GoalTypes.explore;

/**
 * a goal to lead an agent to random location
 */
public class Explore extends CompositeGoal {
    /**
     * set to true when the destination for the exploration has been established
     */
    private boolean waiting;

    /**
     * constructor
     * @param agent the owner of the goal
     */
    public Explore(GameCharacter agent) {
        super(agent, explore);
        waiting = false;
    }

    @Override
    public void activate() {
        status = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        removeAllSubgoals();

        //grab a random location
        Point2D pos = Controller.services.getGraph().getRandomNodeLocation();
        if (pos==null){
            throw new RuntimeException("wrong with getRandomNodeLocation()");
        }

        //and request a path to that position
        agent.getNavigationService().createRequest(pos);
        addSubGoal(new WaitForPath(agent));
        waiting = true;
    }

    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        //process the subgoals
        status = processSubgoals();

        if (status == completed) {
            if (waiting==true){ //we finished waiting for path
                waiting=false;
                status=active;
                removeAllSubgoals();
                addSubGoal(new FollowPath(agent,
                        agent.getNavigationService().getPath()));
            } else { //we finished follow path and request another location to explore
                activate();
            }
        }

        return status;
    }

    @Override
    public void terminate() {
		removeAllSubgoals();
    }

    @Override
    public String toString() {
        String s = "";
        s += "Explore {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
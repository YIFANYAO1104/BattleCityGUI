package com.bham.bc.components.characters.goals.composite.big;


import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;
import com.bham.bc.components.characters.goals.atomic.Goal_WaitForPath;
import com.bham.bc.components.characters.goals.composite.Goal_Composite;
import com.bham.bc.components.characters.goals.composite.helper.Goal_FollowPath;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_explore;

public class Goal_Explore extends Goal_Composite {
    /**
     * set to true when the destination for the exploration has been established
     */
    private boolean waiting;

    public Goal_Explore(GameCharacter pOwner) {
        super(pOwner, goal_explore);
        waiting = false;
    }

    @Override
    public void activate() {
        status = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        RemoveAllSubgoals();

        //grab a random location
        Point2D pos = Controller.services.getGraph().getRandomNodeLocation();
        if (pos==null){
            System.out.println("wrong with getRandomNodeLocation()");
        }

        //and request a path to that position
        agent.getNavigationService().createRequest(pos);
        addSubgoal(new Goal_WaitForPath(agent));
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
                RemoveAllSubgoals();
                addSubgoal(new Goal_FollowPath(agent,
                        agent.getNavigationService().getPath()));
            } else { //we finished follow path and request another location to explore
                activate();
            }
        }

        return status;
    }

    @Override
    public void terminate() {
		RemoveAllSubgoals();
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
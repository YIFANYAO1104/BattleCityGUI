package com.bham.bc.entity.ai.goals.composite.helper;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import javafx.geometry.Point2D;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_move_to_position;

public class Goal_NavigateToPosition extends Goal_Composite {

    /**
     * the position the bot wants to reach
     */
    private Point2D targetPos;
    private boolean waiting;

    public Goal_NavigateToPosition(GameCharacter pBot,
                                   Point2D pos) {

        super(pBot, goal_move_to_position);
        targetPos = pos;
    }

    //the usual suspects
    @Override
    public void activate() {
        status = active;

        //make sure the subgoal list is clear.
        RemoveAllSubgoals();

        if (agent.getNavigationService().createRequest(targetPos)) {
            addSubgoal(new Goal_WaitForPath(agent));
            waiting = true;
        } else {
            status = failed;
        }
    }

    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        //process the subgoals
        status = processSubgoals();

        if (status == completed) {
            if (waiting==true){ //we finished waiting for path
                status=active;
                waiting=false;
                RemoveAllSubgoals();
                addSubgoal(new Goal_FollowPath(agent,
                        agent.getNavigationService().getPath()));
            }
        }

        //if any of the subgoals have failed then this goal re-plans
        reactivateIfFailed();

        return status;
    }

    @Override
    public void terminate() {
		RemoveAllSubgoals();
    }

    @Override
    public String toString() {
        String s = "";
        s += "NVG to Pos {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
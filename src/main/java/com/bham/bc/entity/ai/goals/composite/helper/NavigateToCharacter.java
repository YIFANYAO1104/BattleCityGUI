package com.bham.bc.entity.ai.goals.composite.helper;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.WaitForPath;
import com.bham.bc.entity.ai.goals.composite.CompositeGoal;

import static com.bham.bc.entity.ai.goals.GoalTypes.navigate_to_character;

/**
 * class to define a behavior to hunt a game character
 */
public class NavigateToCharacter extends CompositeGoal {

    /**
     * the position the bot wants to reach
     */
    private GameCharacter target;
    private boolean waiting;

    public NavigateToCharacter(GameCharacter myself,
                               GameCharacter target) {

        super(myself, navigate_to_character);
        this.target = target;
    }

    //the usual suspects
    @Override
    public void activate() {
        status = active;

        //make sure the subgoal list is clear.
        removeAllSubgoals();

        if (agent.getNavigationService().createRequest(target)) {
            addSubGoal(new WaitForPath(agent));
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
                removeAllSubgoals();
                addSubGoal(new FollowPath(agent,
                        agent.getNavigationService().getPath()));
            }
        }

        //if any of the subgoals have failed then this goal re-plans
        reactivateIfFailed();

        return status;
    }

    @Override
    public void terminate() {
		removeAllSubgoals();
    }

    @Override
    public String toString() {
        String s = "";
        s += "NVG to Entity {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
package com.bham.bd.entity.ai.goals.composite.big;


import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.entity.ai.goals.atomic.Goal;
import com.bham.bd.entity.ai.goals.atomic.WaitForPath;
import com.bham.bd.entity.ai.goals.composite.CompositeGoal;
import com.bham.bd.entity.ai.goals.composite.helper.FollowPath;
import com.bham.bd.entity.ai.navigation.ItemType;
import com.bham.bd.entity.ai.navigation.SearchStatus;

import static com.bham.bd.entity.ai.goals.GoalTypes.get_health;

/**
 * class to define behavior for getting a health item
 */
public class GetHealth extends CompositeGoal {

    private ItemType targetType;
    private boolean waiting;

    public GetHealth(GameCharacter pBot, ItemType item) {
        super(pBot, get_health);
        targetType = item;
        waiting=false;
    }

    @Override
    public void activate() {
        status = active;

        //request a path to the item
        agent.getNavigationService().createRequest(targetType);

        addSubGoal(new WaitForPath(agent));
        waiting = true;
        System.out.println("waiting");
    }

    @Override
    public int process() {
        activateIfInactive();

        status = processSubgoals();

        if (waiting==true){ //we are waiting for path
            if (status == completed) {
                if (agent.getNavigationService().peekRequestStatus()== SearchStatus.target_found) {
                    status=active;
                    waiting=false;
                    removeAllSubgoals();
                    addSubGoal(new FollowPath(agent,
                            agent.getNavigationService().getPath()));
                } else {
                    status=failed;
                }
            }
        } else { //we are finding item
            if (!agent.getNavigationService().isTriggerActive()) {
                terminate();
            }
        }

        return status;
    }


    @Override
    public void terminate() {
		removeAllSubgoals();
        status = completed;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Get Item "+targetType+" {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}

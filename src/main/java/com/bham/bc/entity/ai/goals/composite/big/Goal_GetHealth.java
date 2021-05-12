package com.bham.bc.entity.ai.goals.composite.big;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import com.bham.bc.entity.ai.goals.composite.helper.Goal_FollowPath;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.SearchStatus;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_get_health;

public class Goal_GetHealth extends Goal_Composite {

    private ItemType targetType;
    private boolean waiting;

    public Goal_GetHealth(GameCharacter pBot, ItemType item) {
        super(pBot, goal_get_health);
        targetType = item;
        waiting=false;
    }

    @Override
    public void activate() {
        status = active;

        //request a path to the item
        agent.getNavigationService().createRequest(targetType);

        addSubgoal(new Goal_WaitForPath(agent));
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
                    addSubgoal(new Goal_FollowPath(agent,
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

package com.bham.bc.entity.ai.goals.composite.big;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import com.bham.bc.entity.ai.goals.composite.helper.Goal_FollowPath;
import com.bham.bc.entity.ai.navigation.ItemType;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_get_health;

public class Goal_GetItem extends Goal_Composite {

    private ItemType targetType;
    private boolean waiting;

    public Goal_GetItem(GameCharacter pBot, ItemType item) {
        super(pBot, ItemTypeToGoalType(item));
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

    }

    @Override
    public int process() {
        activateIfInactive();

        status = processSubgoals();

            if (waiting==true){ //we are waiting for path
                if (status == completed) {
                    status=active;
                    waiting=false;
                    RemoveAllSubgoals();
                    addSubgoal(new Goal_FollowPath(agent,
                            agent.getNavigationService().getPath()));
                }
            } else { //we are finding item
//                System.out.println(agent.getNavigationService().isTriggerActive());
                if (!agent.getNavigationService().isTriggerActive()) {
                    terminate();
                }
            }



        return status;
    }


    @Override
    public void terminate() {
		RemoveAllSubgoals();
        status = completed;
    }

    /**
     * helper function to change an item type enumeration into a goal type
     */
    public static int ItemTypeToGoalType(ItemType gt) {
        switch (gt) {
            case HEALTH:
                return goal_get_health;
            default:
                throw new RuntimeException("Only heath");

        }//end switch
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

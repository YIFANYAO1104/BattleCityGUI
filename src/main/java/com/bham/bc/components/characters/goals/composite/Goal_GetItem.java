package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;
import com.bham.bc.components.characters.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.navigation.ItemType;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_get_health;
import static com.bham.bc.components.characters.goals.GoalTypes.goal_get_weapon;

public class Goal_GetItem extends Goal_Composite {

    private ItemType targetType;
    private boolean waiting;

    public Goal_GetItem(GameCharacter pBot, ItemType item) {
        super(pBot, ItemTypeToGoalType(item));
        targetType = item;
        waiting=false;
    }

    @Override
    public void Activate() {
        status = active;

        //request a path to the item
        agent.getNavigationService().createRequest(targetType);

        AddSubgoal(new Goal_WaitForPath(agent));
        waiting = true;

    }

    @Override
    public int Process() {
        ActivateIfInactive();

        status = ProcessSubgoals();

            if (waiting==true){ //we are waiting for path
                if (status == completed) {
                    status=active;
                    waiting=false;
                    RemoveAllSubgoals();
                    AddSubgoal(new Goal_FollowPath(agent,
                            agent.getNavigationService().getPath()));
                }
            } else { //we are finding item
//                System.out.println(agent.getNavigationService().isTriggerActive());
                if (!agent.getNavigationService().isTriggerActive()) {
                    Terminate();
                }
            }



        return status;
    }


    @Override
    public void Terminate() {
		RemoveAllSubgoals();
        status = completed;
    }

    /**
     * helper function to change an item type enumeration into a goal type
     */
    protected static int ItemTypeToGoalType(ItemType gt) {
        switch (gt) {
            case HEALTH:

                return goal_get_health;

            case WEAPON:

                return goal_get_weapon;

            default:
                throw new RuntimeException("Only heath and weapon");

        }//end switch
    }

    @Override
    public String toString() {
        String s = "";
        s += "Get Item "+targetType+" {";
        for (Goal m_subGoal : m_SubGoals) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}

package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.navigation.ItemType;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_get_health;
import static com.bham.bc.components.characters.goals.GoalTypes.goal_get_weapon;

public class Goal_GetItem extends Goal_Composite {

    private ItemType m_iItemToGet;
    private boolean waiting;
    /**
     * true if a path to the item has been formulated
     */
    private boolean m_bFollowingPath;

    /**
     * returns true if the bot sees that the item it is heading for has been
     * picked up by an opponent
     */
    public boolean hasItemBeenStolen() {
        //TODO:I WANT TRIGGER ITSELF RATHER THAN THROUGH NS
        if (!agent.getNavigationService().isTriggerActive()) {
            return true;
        }
        return false;
    }

    public Goal_GetItem(GameCharacter pBot, ItemType item) {
        super(pBot, ItemTypeToGoalType(item));
        m_iItemToGet = item;
        m_bFollowingPath = false;
        waiting=false;
    }

    @Override
    public void Activate() {
        status = active;

        //request a path to the item
        agent.getNavigationService().createRequest(m_iItemToGet);

        AddSubgoal(new Goal_WaitForPath(agent));
        waiting = true;

    }

    @Override
    public int Process() {
        ActivateIfInactive();

        status = ProcessSubgoals();
        if (status == completed) {
            if (waiting==true){ //we finished waiting for path
                waiting=false;
                RemoveAllSubgoals();
                AddSubgoal(new Goal_FollowPath(agent,
                        agent.getNavigationService().getPath()));
            } else {
                if (hasItemBeenStolen()) {
                    Terminate();
                }
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
}

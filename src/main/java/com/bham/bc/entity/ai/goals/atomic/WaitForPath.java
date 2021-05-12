package com.bham.bc.entity.ai.goals.atomic;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.SearchStatus;

import static com.bham.bc.entity.ai.goals.GoalTypes.wait_for_path;

/**
 * class to define behavior when an agent is waiting for path
 */
public class WaitForPath extends Goal {


    /**
     * constructor
     * @param agent the owner of the goal
     */
    public WaitForPath(GameCharacter agent) {
        super(agent, wait_for_path);
    }


    @Override
    public void activate() {
        status = active;
    }


    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        SearchStatus searchStatus = agent.getNavigationService().peekRequestStatus();
        if (searchStatus==SearchStatus.target_found){
            status = completed;
        } else if (searchStatus==SearchStatus.target_not_found){
            status = failed;
        }

        return status;
    }

    @Override
    public void terminate() {
        status = completed;
    }

    @Override
    public String toString() {
        String s = "";
        s += "WaitForPath "+status;
        return s;
    }
}
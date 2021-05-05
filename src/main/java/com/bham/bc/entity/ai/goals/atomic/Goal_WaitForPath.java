package com.bham.bc.entity.ai.goals.atomic;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.SearchStatus;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_wait_for_path;

public class Goal_WaitForPath extends Goal {


    //---------------------------- ctor -------------------------------------------
    //-----------------------------------------------------------------------------
    public Goal_WaitForPath(GameCharacter pBot) {
        super(pBot, goal_wait_for_path);
    }

    //the usual suspects
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
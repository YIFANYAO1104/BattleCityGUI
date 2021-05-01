package com.bham.bc.components.characters.goals.atomic;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.composite.Goal_FollowPath;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_seek_to_position;
import static com.bham.bc.components.characters.goals.GoalTypes.goal_wait_for_path;

public class Goal_WaitForPath extends Goal {


    //---------------------------- ctor -------------------------------------------
    //-----------------------------------------------------------------------------
    public Goal_WaitForPath(GameCharacter pBot) {
        super(pBot, goal_wait_for_path);
    }

    //the usual suspects
    @Override
    public void Activate() {
        status = active;
    }


    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        SearchStatus searchStatus = agent.getNavigationService().peekRequestStatus();
        if (searchStatus==SearchStatus.target_found){
            status = completed;
        } else if (searchStatus==SearchStatus.target_not_found){
            status = failed;
        }

        return status;
    }

    @Override
    public void Terminate() {
        status = completed;
    }

    @Override
    public void Render() {
//        if (status == active) {
//            gdi.GreenBrush();
//            gdi.BlackPen();
//            gdi.Circle(targetPos, 3);
//        } else if (status == inactive) {
//
//            gdi.RedBrush();
//            gdi.BlackPen();
//            gdi.Circle(targetPos, 3);
//        }
    }
}
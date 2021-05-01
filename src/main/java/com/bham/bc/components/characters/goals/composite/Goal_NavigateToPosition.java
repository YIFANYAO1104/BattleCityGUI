package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;
import com.bham.bc.components.characters.goals.atomic.Goal_SeekToPosition;
import com.bham.bc.components.characters.goals.atomic.Goal_WaitForPath;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_move_to_position;

public class Goal_NavigateToPosition extends Goal_Composite {

    /**
     * the position the bot wants to reach
     */
    private Point2D m_vDestination;
    private boolean waiting;

    public Goal_NavigateToPosition(GameCharacter pBot,
                                   Point2D pos) {

        super(pBot, goal_move_to_position);
        m_vDestination = pos;
    }

    //the usual suspects
    @Override
    public void Activate() {
        status = active;

        //make sure the subgoal list is clear.
        RemoveAllSubgoals();

        if (agent.getNavigationService().createRequest(m_vDestination)) {
            AddSubgoal(new Goal_WaitForPath(agent));
            waiting = true;
        } else {
            status = failed;
        }
    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        //process the subgoals
        status = ProcessSubgoals();

        if (status == completed) {
            if (waiting==true){ //we finished waiting for path
                status=active;
                waiting=false;
                RemoveAllSubgoals();
                AddSubgoal(new Goal_FollowPath(agent,
                        agent.getNavigationService().getPath()));
            }
        }

        //if any of the subgoals have failed then this goal re-plans
        ReactivateIfFailed();

        return status;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
    }

    @Override
    public void Render() {
//        //forward the request to the subgoals
//        super.Render();
//
//        //draw a bullseye
//        gdi.BlackPen();
//        gdi.BlueBrush();
//        gdi.Circle(m_vDestination, 6);
//        gdi.RedBrush();
//        gdi.RedPen();
//        gdi.Circle(m_vDestination, 4);
//        gdi.YellowBrush();
//        gdi.YellowPen();
//        gdi.Circle(m_vDestination, 2);
    }

    @Override
    public String toString() {
        String s = "";
        s += "NVG to Pos {";
        for (Goal m_subGoal : m_SubGoals) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
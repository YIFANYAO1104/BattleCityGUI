package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_hunt_target;


public class Goal_HuntTarget extends Goal_Composite {

    /**
     * this value is set to true if the last visible position of the target bot
     * has been searched without success
     */
    private boolean m_bLVPTried;

    public Goal_HuntTarget(GameCharacter pBot) {
        super(pBot, goal_hunt_target);
        m_bLVPTried = false;
    }

    //the usual suspects
    @Override
    public void Activate() {
        status = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        RemoveAllSubgoals();

        //if the target alive
        if (agent.getTargetingSystem().isTargetBotPresent()) {
            System.out.println("Goal_NavigateToPosition");
            AddSubgoal(new Goal_NavigateToPosition(agent, agent.getTargetingSystem().getTargetBot().getCenterPosition()));
        } //if their is no active target then this goal can be removed from the queue
        else {
            System.out.println("target");
            status = completed;
        }

    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        status = ProcessSubgoals();

        //if target is in view this goal is satisfied
        if (agent.getTargetingSystem().isTargetBotWithinFOV()) {
            System.out.println("inmyvision");
            status = completed;
        }

        return status;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
    }

    @Override
    public void Render() {
        //define(SHOW_LAST_RECORDED_POSITION);
//        if (def(SHOW_LAST_RECORDED_POSITION)) {
//            //render last recorded position as a green circle
//            if (agent.getTargetingSystem().isTargetPresent()) {
//                gdi.GreenPen();
//                gdi.RedBrush();
//                gdi.Circle(agent.getTargetingSystem().GetLastRecordedPosition(), 3);
//            }
//        }
    }

    @Override
    public String toString() {
        String s = "";
        s += "Hunt Target {";
        for (Goal m_subGoal : m_SubGoals) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
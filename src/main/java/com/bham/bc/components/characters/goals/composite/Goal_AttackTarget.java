package com.bham.bc.components.characters.goals.composite;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;
import com.bham.bc.components.characters.goals.atomic.Goal_SeekToPosition;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_attack_target;


public class Goal_AttackTarget extends Goal_Composite {

    public Goal_AttackTarget(GameCharacter pOwner) {
        super(pOwner, goal_attack_target);
    }

    @Override
    public void Activate() {
        status = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        RemoveAllSubgoals();

        //if target dead, time to get off work
        if (!agent.getTargetingSystem().isTargetBotPresent()) {
            status = completed;
            return;
        }

        //if we could see our target, we approach it
        if (agent.getTargetingSystem().isTargetBotWalkable()) {
            if (!agent.getTargetingSystem().isReachingSafeDistance()){
                AddSubgoal(new Goal_SeekToPosition(agent, agent.getTargetingSystem().getTargetBot().getCenterPosition()));
            }
        } //if the target is not visible, go hunt it.
        else {
            AddSubgoal(new Goal_NavigateToPosition(agent, agent.getTargetingSystem().getTargetBot().getCenterPosition()));
        }
    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        //process the subgoals
        status = ProcessSubgoals();

        //if bot alive/within checking range
        if (agent.getTargetingSystem().isTargetBotPresent()){
            //brake to avoid collision
            if (agent.getTargetingSystem().isTargetBotVisable()) {
                agent.getSteering().seekOff();
                agent.brake();
            } else {//If target evade, we chase again
                agent.getSteering().seekOn();
            }
        }else {
            status = completed;
        }

        ReactivateIfFailed();

        return status;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
        status = completed;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Attack Target {";
        for (Goal m_subGoal : m_SubGoals) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
package com.bham.bc.entity.ai.goals.composite.big;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.Goal_SeekToPosition;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import com.bham.bc.entity.ai.goals.composite.helper.Goal_NavigateToEntity;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_attack_closest_target;

/**
 * class to define how an agent could attack target
 */
public class Goal_AttackClosestTarget extends Goal_Composite {

    /**
     * constructor
     * @param agent the owner of the goal
     */
    public Goal_AttackClosestTarget(GameCharacter agent) {
        super(agent, goal_attack_closest_target);
    }

    @Override
    public void activate() {
        status = active;

        agent.getTargetingSystem().attackTargetOn();
        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        removeAllSubgoals();

        //if target dead, time to get off work
        if (!agent.getTargetingSystem().isTargetBotPresent()) {
            status = completed;
            return;
        }

        //if we could see our target, we approach it
        if (agent.getTargetingSystem().isTargetBotWalkable()) {
            if (!agent.getTargetingSystem().isReachingSafeDistance()){
                addSubgoal(new Goal_SeekToPosition(agent, agent.getTargetingSystem().getTargetBot().getCenterPosition()));
            }
        } //if the target is not visible, ask for a path
        else {
            addSubgoal(new Goal_NavigateToEntity(agent, agent.getTargetingSystem().getTargetBot()));
        }
    }

    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        //process the subgoals
        status = processSubgoals();

        //if bot alive/within checking range
        if (agent.getTargetingSystem().isTargetBotPresent()){
            //brake to avoid collision
            if (agent.getTargetingSystem().isTargetBotVisiable()) {
                agent.getSteering().seekOff();
                agent.brake();
            } else {//If target evade, we chase again
                agent.getSteering().seekOn();
            }
        }else {
            status = completed;
        }

        reactivateIfFailed();

        return status;
    }

    @Override
    public void terminate() {
		removeAllSubgoals();
        agent.getTargetingSystem().attackTargetOff();
        agent.getSteering().seekOff();
        status = completed;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Attack Closest Target {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}
package com.bham.bc.entity.ai.goals;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.UtilityCalculator;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import com.bham.bc.entity.ai.goals.composite.big.Goal_AttackClosestTarget;
import com.bham.bc.entity.ai.goals.composite.big.Goal_Explore;
import com.bham.bc.entity.ai.goals.composite.big.Goal_GetHealth;
import com.bham.bc.entity.ai.navigation.ItemType;

import static com.bham.bc.entity.ai.goals.GoalTypes.*;

/**
 * define how an agent make decisions
 */
public class Goal_Think extends Goal_Composite {

    UtilityCalculator uc;

    public Goal_Think(GameCharacter agent) {
        super(agent, goal_think);
        uc = new UtilityCalculator(agent);
    }

    /**
     * this method pick a most desirable goal for the agent
     */
    public void decideOnGoals() {
//        int r = RandomEnhanced.randInt(0,1);
//        if (r==0) {
//            int goal = uc.getDecision();
//            switch (goal){
//                case goal_explore: addGoalExplore();break;
//                case goal_attack_closest_target: addGoalAttackClosestTarget();break;
//                case goal_get_health:
//                    addGoalGetHealth(ItemType.HEALTH);break;
//            }
//        } else {
//            int k = RandomEnhanced.randInt(0,2);
//            switch (k){
//                case 0: addGoalExplore();break;
//                case 1: addGoalAttackClosestTarget();break;
//                case 2:
//                    addGoalGetHealth(ItemType.HEALTH);break;
//            }
//        }
        int goal = uc.getDecision();
        switch (goal){
            case goal_explore: addGoalExplore();break;
            case goal_attack_closest_target: addGoalAttackClosestTarget();break;
            case goal_get_health: addGoalGetHealth();break;
        }
    }

    /**
     * returns true if the goal type passed as a parameter is the same as this
     * goal or any of its subgoals
     */
    public boolean notPresent(int GoalType) {
        if (!subGoalList.isEmpty()) {
            return subGoalList.getFirst().getType() != GoalType;
        }

        return true;
    }

    //the usual suspects
    /**
     * processes the subgoals
     */
    @Override
    public int process() {
        activateIfInactive();

        int subgoalStatus = processSubgoals();

        if (subgoalStatus == completed || subgoalStatus == failed) {
            status = inactive;
        }

        return status;
    }

    @Override
    public void activate() {
        decideOnGoals();
        status = active;
    }

    @Override
    public void terminate() {
		removeAllSubgoals();
    }

    public void addGoalGetHealth() {
        if (notPresent(goal_get_health)) {
            removeAllSubgoals();
            //PlanB, addSubgoal() is add first
            addSubgoal(new Goal_Explore(agent));
            //PlanA
            addSubgoal(new Goal_GetHealth(agent,ItemType.HEALTH));
        }
    }

    public void addGoalExplore() {
        if (notPresent(goal_explore)) {
            removeAllSubgoals();
            addSubgoal(new Goal_Explore(agent));
        }
    }

    public void addGoalAttackClosestTarget() {
//        if (notPresent(goal_attack_cloest_target)) {
            removeAllSubgoals();
            addSubgoal(new Goal_AttackClosestTarget(agent));
//        }
    }

    @Override
    public String toString() {
        return subGoalList.toString();
    }
}
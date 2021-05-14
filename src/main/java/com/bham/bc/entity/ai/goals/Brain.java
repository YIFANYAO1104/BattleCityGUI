package com.bham.bc.entity.ai.goals;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.composite.CompositeGoal;
import com.bham.bc.entity.ai.goals.composite.big.AttackClosestTarget;
import com.bham.bc.entity.ai.goals.composite.big.Explore;
import com.bham.bc.entity.ai.goals.composite.big.GetHealth;
import com.bham.bc.entity.ai.navigation.ItemType;

import static com.bham.bc.entity.ai.goals.GoalTypes.*;

/**
 * define how an agent make decisions
 */
public class Brain extends CompositeGoal {

    /**
     * the util class for calculating utility
     */
    private UtilityCalculator uc;

    /**
     * Constructs the Goal_Think Object and initialize it
     * @param agent the owner of the goal
     */
    public Brain(GameCharacter agent) {
        super(agent, brain);
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
            case explore: addGoalExplore();break;
            case attack_closest_target: addGoalAttackClosestTarget();break;
            case get_health: addGoalGetHealth();break;
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

    @Override
    public void activate() {
        decideOnGoals();
        status = active;
    }

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
    public void terminate() {
		removeAllSubgoals();
    }


    private void addGoalAttackClosestTarget() {
//        if (notPresent(goal_attack_cloest_target)) {
        removeAllSubgoals();
        addSubGoal(new AttackClosestTarget(agent));
//        }
    }

    private void addGoalGetHealth() {
        if (notPresent(get_health)) {
            removeAllSubgoals();
            //PlanB, addSubgoal() is add first
            addSubGoal(new Explore(agent));
            //PlanA
            addSubGoal(new GetHealth(agent,ItemType.HEALTH));
        }
    }

    private void addGoalExplore() {
        if (notPresent(explore)) {
            removeAllSubgoals();
            addSubGoal(new Explore(agent));
        }
    }

    @Override
    public String toString() {
        return subGoalList.toString();
    }
}
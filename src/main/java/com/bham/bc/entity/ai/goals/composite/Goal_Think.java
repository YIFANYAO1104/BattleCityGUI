package com.bham.bc.entity.ai.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.composite.big.Goal_AttackClosestTarget;
import com.bham.bc.entity.ai.goals.composite.big.Goal_AttackWeakestTarget;
import com.bham.bc.entity.ai.goals.composite.big.Goal_Explore;
import com.bham.bc.entity.ai.goals.composite.big.Goal_GetItem;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.utils.RandomEnhanced;

import static com.bham.bc.entity.ai.goals.GoalTypes.*;

public class Goal_Think extends Goal_Composite {

    public Goal_Think(GameCharacter pBot) {
        super(pBot, goal_think);

    }

//----------------------------- dtor ------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * this method iterates through each goal evaluator and selects the one that
     * has the highest score as the current goal
     */
    public void decideOnGoals() {
        int index = RandomEnhanced.randInt(0,3);
        switch (index){
            case 0:
                addGoalGetItem(ItemType.HEALTH);break;
            case 1:
                addGoalAttackClosestTarget();break;
            case 2:
                addGoalExplore();break;
            case 3:
                addGoalAttackWeakestTarget();break;
        }
//        AddGoal_AttackTarget();
//        AddGoal_Explore();
//        AddGoal_GetItem(ItemType.HEALTH);
//        addGoalAttackClosestTarget();
//        addGoalAttackWeakestTarget();
        assert (index >=0 && index<=3) : "<Goal_Think::Arbitrate>: no evaluator selected";
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

//        System.out.println(subGoalList);
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
		RemoveAllSubgoals();
    }

    public void addGoalGetItem(ItemType itemType) {
        if (notPresent(Goal_GetItem.ItemTypeToGoalType(itemType))) {
            RemoveAllSubgoals();
            addSubgoal(new Goal_GetItem(agent, itemType));
        }
    }

    public void addGoalExplore() {
        if (notPresent(goal_explore)) {
            RemoveAllSubgoals();
            addSubgoal(new Goal_Explore(agent));
        }
    }

    public void addGoalAttackClosestTarget() {
        if (notPresent(goal_attack_cloest_target)) {
            RemoveAllSubgoals();
            addSubgoal(new Goal_AttackClosestTarget(agent));
        }
    }

    public void addGoalAttackWeakestTarget() {
        if (notPresent(goal_attack_weakest_target)) {
            RemoveAllSubgoals();
            addSubgoal(new Goal_AttackWeakestTarget(agent));
        }
    }
}
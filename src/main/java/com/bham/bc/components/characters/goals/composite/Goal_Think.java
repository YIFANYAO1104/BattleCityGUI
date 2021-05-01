package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.utils.RandomEnhanced;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.*;

public class Goal_Think extends Goal_Composite {

    private int goalNum;

    public Goal_Think(GameCharacter pBot) {
        super(pBot, goal_think);
        goalNum = 4;

    }

//----------------------------- dtor ------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * this method iterates through each goal evaluator and selects the one that
     * has the highest score as the current goal
     */
    public void Arbitrate() {
        int index = RandomEnhanced.randInt(0,4);

//        switch (index){
//            case 0:AddGoal_GetItem(ItemType.WEAPON);break;
//            case 1:AddGoal_GetItem(ItemType.HEALTH);break;
//            case 2:AddGoal_AttackTarget();break;
//            case 3:AddGoal_Explore();break;
//        }
//        AddGoal_AttackTarget();
//        AddGoal_Explore();
        AddGoal_GetItem(ItemType.HEALTH);
        assert (index >=0 && index<4) : "<Goal_Think::Arbitrate>: no evaluator selected";
    }

    /**
     * returns true if the goal type passed as a parameter is the same as this
     * goal or any of its subgoals
     */
    public boolean notPresent(int GoalType) {
        if (!m_SubGoals.isEmpty()) {
            return m_SubGoals.getFirst().GetType() != GoalType;
        }

        return true;
    }

    //the usual suspects
    /**
     * processes the subgoals
     */
    @Override
    public int Process() {
        ActivateIfInactive();

        System.out.println(m_SubGoals);
        int SubgoalStatus = ProcessSubgoals();

        if (SubgoalStatus == completed || SubgoalStatus == failed) {
            status = inactive;
        }

        return status;
    }

    @Override
    public void Activate() {
        Arbitrate();
        status = active;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
    }

    //top level goal types
    public void AddGoal_MoveToPosition(Point2D pos) {
        AddSubgoal(new Goal_NavigateToPosition(agent, pos));
    }

    public void AddGoal_GetItem(ItemType itemType) {
        if (notPresent(Goal_GetItem.ItemTypeToGoalType(itemType))) {
            RemoveAllSubgoals();
            AddSubgoal(new Goal_GetItem(agent, itemType));
        }
    }

    public void AddGoal_Explore() {
        if (notPresent(goal_explore)) {
            RemoveAllSubgoals();
            AddSubgoal(new Goal_Explore(agent));
        }
    }

    public void AddGoal_AttackTarget() {
        if (notPresent(goal_attack_target)) {
            RemoveAllSubgoals();
            AddSubgoal(new Goal_AttackTarget(agent));
        }
    }

    /**
     * this adds the MoveToPosition goal to the *back* of the subgoal list.
     */
    public void QueueGoal_MoveToPosition(Point2D pos) {
        m_SubGoals.add(new Goal_NavigateToPosition(agent, pos));
    }

    /**
     * this renders the evaluations (goal scores) at the specified location
     */
    public void RenderEvaluations(int left, int top) {
//        gdi.TextColor(Cgdi.black);
//
//        Iterator<Goal_Evaluator> it = m_Evaluators.iterator();
//        while (it.hasNext()) {
//            Goal_Evaluator curDes = it.next();
//            curDes.RenderInfo(new Point2D(left, top), agent);
//
//            left += 75;
//        }
    }

    @Override
    public void Render() {
//        Iterator<Goal<GameCharacter>> it = m_SubGoals.iterator();
//        while (it.hasNext()) {
//            it.next().Render();;
//        }
    }
}
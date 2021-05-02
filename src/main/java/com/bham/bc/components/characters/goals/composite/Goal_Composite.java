package com.bham.bc.components.characters.goals.composite;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class Goal_Composite extends Goal {


    /**
     * composite goals may have any number of subgoals
     */
    protected LinkedList<Goal> subGoalList = new LinkedList<>();

    /**
     * processes any subgoals that may be present
     *
     * this method first removes any completed goals from the front of the
     * subgoal list. It then processes the next goal in the list (if there is
     * one)
     */
    protected int processSubgoals() {
        //remove all completed and failed goals from the front of the subgoal list
        while (!subGoalList.isEmpty()
                && (subGoalList.getFirst().isComplete() || subGoalList.getFirst().hasFailed())) {
            subGoalList.getFirst().terminate();
            subGoalList.removeFirst();
        }

        //if any subgoals remain, process the one at the front of the list
        if (!subGoalList.isEmpty()) {
            //grab the status of the front-most subgoal
            int StatusOfSubGoals = subGoalList.getFirst().process();

            //we have to test for the special case where the front-most subgoal
            //reports 'completed' *and* the subgoal list contains additional goals.When
            //this is the case, to ensure the parent keeps processing its subgoal list
            //we must return the 'active' status.
            if (StatusOfSubGoals == completed && subGoalList.size() > 1) {
                return active;
            }

            return StatusOfSubGoals;
        } //no more subgoals to process - return 'completed'
        else {
            return completed;
        }
    }

    public Goal_Composite(GameCharacter pE, int type) {
        super(pE, type);
    }

    /**
     * logic to run when the goal is activated.
     */
    @Override
    public abstract void activate();

    /**
     * logic to run each update-step.
     */
    @Override
    public abstract int process();

    /**
     * logic to run prior to the goal's destruction
     */
    @Override
    public abstract void terminate();

    /**
     * adds a subgoal to the front of the subgoal list
     */
    @Override
    public void addSubgoal(Goal g) {
        //add the new goal to the front of the list
        subGoalList.addFirst(g);
    }

    /**
     * this method iterates through the subgoals and calls each one's Terminate
     * method before deleting the subgoal and removing it from the subgoal list
     */
    public void RemoveAllSubgoals() {
        Iterator<Goal> it = subGoalList.iterator();
        while (it.hasNext()) {
            it.next().terminate();
            //it.remove();
        }

        subGoalList.clear();
    }
}
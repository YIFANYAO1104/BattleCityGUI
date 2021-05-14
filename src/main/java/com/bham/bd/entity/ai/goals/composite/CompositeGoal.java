package com.bham.bd.entity.ai.goals.composite;

import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.entity.ai.goals.atomic.Goal;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * abstract class to define composite goal
 */
public abstract class CompositeGoal extends Goal {

    /**
     * a list of goals that the agent have
     */
    protected LinkedList<Goal> subGoalList;

    /**
     * constructor
     * @param agent the owner of the goal
     * @param type one of active, inactive,completed, failed
     */
    public CompositeGoal(GameCharacter agent, int type) {
        super(agent, type);
        subGoalList = new LinkedList<>();
    }

    /**
     * processes the first subgoal and remove if done
     */
    protected int processSubgoals() {

        //remove all completed and failed goals from the front of the subgoal list
        while (!subGoalList.isEmpty()
                && (subGoalList.getFirst().isComplete() || subGoalList.getFirst().isFailed())) {
            subGoalList.getFirst().terminate();
            subGoalList.removeFirst();
        }

        //process the front-most goal
        if (!subGoalList.isEmpty()) {
            //grab the status of the front-most subgoal
            int firstGoalStatus = subGoalList.getFirst().process();

            //if there been more goals that needs to be processed
            if (firstGoalStatus == completed && subGoalList.size() > 1) {
                return active;
            }

            return firstGoalStatus;
        } //no more subgoals to process
        else {
            return completed;
        }
    }



    @Override
    public abstract void activate();

    @Override
    public abstract int process();

    @Override
    public abstract void terminate();

    @Override
    public void addSubGoal(Goal g) {
        subGoalList.addFirst(g);
    }

    /**
     * terminate each subgoals
     */
    public void removeAllSubgoals() {
        Iterator<Goal> it = subGoalList.iterator();
        while (it.hasNext()) {
            it.next().terminate();
        }

        subGoalList.clear();
    }
}
package com.bham.bc.entity.ai.goals.composite.helper;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.goals.atomic.Goal;
import com.bham.bc.entity.ai.goals.atomic.Goal_TraverseEdge;
import com.bham.bc.entity.ai.goals.composite.Goal_Composite;
import com.bham.bc.entity.ai.navigation.PathEdge;

import java.util.LinkedList;
import java.util.List;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_follow_path;

/**
 * The class to define how an agent follow a path
 */
public class Goal_FollowPath extends Goal_Composite {

    /**
     * a copy of the path
     */
    private LinkedList<PathEdge> pathList;

    //the usual suspects
    public Goal_FollowPath(GameCharacter pBot, List<PathEdge> path) {
        super(pBot, goal_follow_path);
        pathList = new LinkedList<>(path);
    }

    @Override
    public void activate() {
        status = active;

        PathEdge edge = pathList.getFirst();
        pathList.removeFirst();
        addSubgoal(new Goal_TraverseEdge(agent, edge));
    }

    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();

        status = processSubgoals();

        if (status == completed && !pathList.isEmpty()) {
            activate();
        }

        return status;
    }

    @Override
    public void terminate() {
		removeAllSubgoals();
		agent.brake();
    }

    @Override
    public String toString() {
        String s = "";
        s += "Follow Path {";
        for (Goal m_subGoal : subGoalList) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}

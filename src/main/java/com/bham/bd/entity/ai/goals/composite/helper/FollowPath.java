package com.bham.bd.entity.ai.goals.composite.helper;

import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.entity.ai.goals.atomic.Goal;
import com.bham.bd.entity.ai.goals.atomic.FollowEdge;
import com.bham.bd.entity.ai.goals.composite.CompositeGoal;
import com.bham.bd.entity.ai.navigation.PathEdge;

import java.util.LinkedList;
import java.util.List;

import static com.bham.bd.entity.ai.goals.GoalTypes.follow_path;

/**
 * The class to define how an agent follow a path
 */
public class FollowPath extends CompositeGoal {

    /**
     * a copy of the path
     */
    private LinkedList<PathEdge> pathList;

    //the usual suspects
    public FollowPath(GameCharacter pBot, List<PathEdge> path) {
        super(pBot, follow_path);
        pathList = new LinkedList<>(path);
    }

    @Override
    public void activate() {
        status = active;

        PathEdge edge = pathList.getFirst();
        pathList.removeFirst();
        addSubGoal(new FollowEdge(agent, edge));
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

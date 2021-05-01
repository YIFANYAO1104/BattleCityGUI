package com.bham.bc.components.characters.goals.composite;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal;
import com.bham.bc.components.characters.goals.atomic.Goal_TraverseEdge;
import com.bham.bc.entity.ai.navigation.PathEdge;
import com.bham.bc.entity.graph.edge.GraphEdge;

import java.util.LinkedList;
import java.util.List;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_follow_path;


public class Goal_FollowPath extends Goal_Composite {

    /**
     * a local copy of the path returned by the path planner
     */
    private LinkedList<PathEdge> pathList;

    //the usual suspects
    public Goal_FollowPath(GameCharacter pBot, List<PathEdge> path) {
        super(pBot, goal_follow_path);
        pathList = new LinkedList<>(path);
    }

    @Override
    public void Activate() {
        status = active;

        //get a reference to the next edge
        PathEdge edge = pathList.getFirst();

        //remove the edge from the path
        pathList.removeFirst();

        //some edges specify that the bot should use a specific behavior when
        //following them. This switch statement queries the edge behavior flag and
        //adds the appropriate goals/s to the subgoal list.
        AddSubgoal(new Goal_TraverseEdge(agent, edge, pathList.isEmpty()));
    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        status = ProcessSubgoals();

        //if there are no subgoals present check to see if the path still has edges.
        //remaining. If it does then call activate to grab the next edge.
        if (status == completed && !pathList.isEmpty()) {
            Activate();
        }

        return status;
    }

    @Override
    public void Render() {
//        //render all the path waypoints remaining on the path list
//
//        Iterator<PathEdge> it = m_Path.iterator();
//
//        while (it.hasNext()) {
//            PathEdge path = it.next();
//            gdi.BlackPen();
//            gdi.LineWithArrow(path.Source(), path.Destination(), 5);
//
//            gdi.RedBrush();
//            gdi.BlackPen();
//            gdi.Circle(path.Destination(), 3);
//        }
//
//        //forward the request to the subgoals
//        super.Render();
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
		agent.brake();
    }

    @Override
    public String toString() {
        String s = "";
        s += "Follow Path {";
        for (Goal m_subGoal : m_SubGoals) {
            s += m_subGoal.toString()+" ";
        }
        s += "}"+status;
        return s;
    }
}

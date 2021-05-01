package com.bham.bc.components.characters.goals.composite;


import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.goals.atomic.Goal_SeekToPosition;
import com.bham.bc.components.characters.goals.atomic.Goal_WaitForPath;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_explore;

public class Goal_Explore extends Goal_Composite {

    private Point2D m_CurrentDestination = new Point2D(0,0);
    /**
     * set to true when the destination for the exploration has been established
     */
    private boolean m_bDestinationIsSet;
    private boolean waiting;

    public Goal_Explore(GameCharacter pOwner) {
        super(pOwner, goal_explore);
        m_bDestinationIsSet = false;
        waiting = false;
    }

    @Override
    public void Activate() {
        status = active;

        //if this goal is reactivated then there may be some existing subgoals that
        //must be removed
        RemoveAllSubgoals();

        if (!m_bDestinationIsSet) {
            //grab a random location
            m_CurrentDestination = Controller.services.getGraph().getRandomNodeLocation();
            if (m_CurrentDestination==null){
                System.out.println("wrong with getRandomNodeLocation()");
            }

            m_bDestinationIsSet = true;
        }

        //and request a path to that position
        agent.getNavigationService().createRequest(m_CurrentDestination);
        AddSubgoal(new Goal_WaitForPath(agent));
        waiting = true;
    }

    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();

        //process the subgoals
        status = ProcessSubgoals();

        if (status == completed) {
            if (waiting==true){ //we finished waiting for path
                waiting=false;
                RemoveAllSubgoals();
                AddSubgoal(new Goal_FollowPath(agent,
                        agent.getNavigationService().getPath()));
            } else { //we finished follow path
                Activate();
            }
        }

        return status;
    }

    @Override
    public void Terminate() {
		RemoveAllSubgoals();
    }
}
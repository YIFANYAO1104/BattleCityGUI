package com.bham.bc.entity.ai.goals.atomic;

import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;

import static com.bham.bc.entity.ai.goals.GoalTypes.goal_seek_to_position;

public class Goal_SeekToPosition extends Goal {

    /**
     * the position the bot is moving to
     */
    private Point2D targetPos;

    //---------------------------- ctor -------------------------------------------
    //-----------------------------------------------------------------------------
    public Goal_SeekToPosition(GameCharacter pBot, Point2D target) {
        super(pBot, goal_seek_to_position);
        this.targetPos = target;
    }

    //the usual suspects
    @Override
    public void activate() {
        status = active;

        agent.getSteering().setTarget(targetPos);

        agent.getSteering().seekOn();
    }


    @Override
    public int process() {
        //if status is inactive, call Activate()
        activateIfInactive();


        if (agent.isReached(targetPos)) {
            status = completed;
        }

        return status;
    }

    @Override
    public void terminate() {
        agent.getSteering().seekOff();
        agent.brake();
        status = completed;
    }


    @Override
    public String toString() {
        return "SeektoPos"+status;
    }
}
package com.bham.bc.components.characters.goals.atomic;

import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;

import static com.bham.bc.components.characters.goals.GoalTypes.goal_seek_to_position;

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
    public void Activate() {
        status = active;

        agent.getSteering().setTarget(targetPos);

        agent.getSteering().seekOn();
    }


    @Override
    public int Process() {
        //if status is inactive, call Activate()
        ActivateIfInactive();


        if (agent.isReached(targetPos)) {
            status = completed;
        }

        return status;
    }

    @Override
    public void Terminate() {
        agent.getSteering().seekOff();
        agent.brake();
        //TODO: ADD ARRIVE
//        agent.getSteering().arriveOff();

        status = completed;
    }

    @Override
    public void Render() {
//        if (status == active) {
//            gdi.GreenBrush();
//            gdi.BlackPen();
//            gdi.Circle(targetPos, 3);
//        } else if (status == inactive) {
//
//            gdi.RedBrush();
//            gdi.BlackPen();
//            gdi.Circle(targetPos, 3);
//        }
    }


    @Override
    public String toString() {
        return "SeektoPos"+status;
    }
}
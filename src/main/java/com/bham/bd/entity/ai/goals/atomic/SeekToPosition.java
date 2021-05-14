package com.bham.bd.entity.ai.goals.atomic;

import com.bham.bd.components.characters.GameCharacter;
import javafx.geometry.Point2D;

import static com.bham.bd.entity.ai.goals.GoalTypes.seek_to_position;

/**
 * class for defining behavior for seek to position
 */
public class SeekToPosition extends Goal {

    /**
     * the position the bot is moving to
     */
    private Point2D targetPos;

    /**
     * constructor
     * @param agent the owner of the goal
     * @param target the target that the agent want to reach using seek steering behavior
     */
    public SeekToPosition(GameCharacter agent, Point2D target) {
        super(agent, seek_to_position);
        this.targetPos = target;
    }

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
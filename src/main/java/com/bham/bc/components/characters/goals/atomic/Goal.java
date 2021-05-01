package com.bham.bc.components.characters.goals.atomic;


import com.bham.bc.components.characters.GameCharacter;

abstract public class Goal {

    public static final int active = 0;
    public static final int inactive = 1;
    public static final int completed = 2;
    public static final int failed = 3;
    /**
     * an enumerated type specifying the type of goal
     */
    protected int goalType;
    /**
     * a pointer to the entity that owns this goal
     */
    protected GameCharacter agent;
    /**
     * an enumerated value indicating the goal's status (active, inactive,
     * completed, failed)
     */
    protected int status;


    /* the following methods were created to factor out some of the commonality
     in the implementations of the Process method() */
    /**
     * if status = inactive this method sets it to active and calls
     * Activate()
     */
    protected void ActivateIfInactive() {
        if (isInactive()) {
            Activate();
        }
    }

    //if status is failed this method sets it to inactive so that the goal
    //will be reactivated (and therefore re-planned) on the next update-step.
    protected void ReactivateIfFailed() {
        if (hasFailed()) {
            status = inactive;
        }
    }

    /**
     * note how goals start off in the inactive state
     */
    public Goal(GameCharacter pE, int type) {
        goalType = type;
        agent = pE;
        status = inactive;
    }

    /**
     * logic to run when the goal is activated.
     */
    public abstract void Activate();

    /**
     * logic to run each update-step
     */
    public abstract int Process();

    /**
     * logic to run when the goal is satisfied. (typically used to switch off
     * any active steering behaviors)
     */
    public abstract void Terminate();

    /**
     * a Goal is atomic and cannot aggregate subgoals yet we must implement this
     * method to provide the uniform interface required for the goal hierarchy.
     */
    public void AddSubgoal(Goal g) {
        throw new RuntimeException("Cannot add goals to atomic goals");
    }

    public boolean isComplete() {
        return status == completed;
    }

    public boolean isActive() {
        return status == active;
    }

    public boolean isInactive() {
        return status == inactive;
    }

    public boolean hasFailed() {
        return status == failed;
    }

    public int GetType() {
        return goalType;
    }

    /**
     * this is used to draw the name of the goal at the specific position used
     * for debugging
     */
//    public void RenderAtPos(Point2D pos, TypeToString tts) {
//        pos.y += 15;
//        gdi.TransparentText();
//        if (isComplete()) {
//            gdi.TextColor(0, 255, 0);
//        }
//        if (isInactive()) {
//            gdi.TextColor(0, 0, 0);
//        }
//        if (hasFailed()) {
//            gdi.TextColor(255, 0, 0);
//        }
//        if (isActive()) {
//            gdi.TextColor(0, 0, 255);
//        }
//
//        gdi.TextAtPos(pos.x, pos.y, tts.Convert(GetType()));
//    }

    /**
     * used to render any goal specific information
     */
    public void Render() {
    }
}
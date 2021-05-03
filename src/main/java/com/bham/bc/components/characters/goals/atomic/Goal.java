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


    /**
     * note how goals start off in the inactive state
     */
    public Goal(GameCharacter agent, int type) {
        goalType = type;
        this.agent = agent;
        status = inactive;
    }

    /**
     * if status = inactive this method sets it to active and calls
     * Activate()
     */
    protected void activateIfInactive() {
        if (isInactive()) {
            activate();
        }
    }

    //if status is failed this method sets it to inactive so that the goal
    //will be reactivated (and therefore re-planned) on the next update-step.
    protected void reactivateIfFailed() {
        if (hasFailed()) {
            status = inactive;
        }
    }

    /**
     * logic to run when the goal is activated.
     */
    public abstract void activate();

    /**
     * logic to run each update-step
     */
    public abstract int process();

    /**
     * logic to run when the goal is satisfied. (typically used to switch off
     * any active steering behaviors)
     */
    public abstract void terminate();

    /**
     * a Goal is atomic and cannot aggregate subgoals yet we must implement this
     * method to provide the uniform interface required for the goal hierarchy.
     */
    public void addSubgoal(Goal g) {
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

    public int getType() {
        return goalType;
    }
}
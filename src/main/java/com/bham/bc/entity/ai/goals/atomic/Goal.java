package com.bham.bc.entity.ai.goals.atomic;


import com.bham.bc.components.characters.GameCharacter;

/**
 * The root class of all goals
 */
abstract public class Goal {

    public static final int active = 0;
    public static final int inactive = 1;
    public static final int completed = 2;
    public static final int failed = 3;
    /**
     * a mark for specifying the goal's type
     */
    protected int goalType;
    /**
     * the owner of this goal
     */
    protected GameCharacter agent;

    /**
     * should be one of active, inactive,completed, failed
     */
    protected int status;


    /**
     * Constructs the goal object and initialize it
     * @param agent the owner of the goal
     * @param type one of active, inactive,completed, failed
     */
    public Goal(GameCharacter agent, int type) {
        goalType = type;
        this.agent = agent;
        status = inactive;
    }

    /**
     * if status == inactive, then this method calls activate
     */
    protected void activateIfInactive() {
        if (isInactive()) {
            activate();
        }
    }

    /**
     * if status == failed, then the status was set to inactive
     * the current goal will be re-planned on the next game loop
     */
    protected void reactivateIfFailed() {
        if (hasFailed()) {
            status = inactive;
        }
    }

    /**
     * specify what an agent should do when an goal starts
     */
    public abstract void activate();

    /**
     * specify what an agent should do when an goal is on
     */
    public abstract int process();

    /**
     * specify what an agent should do when an goal ends
     */
    public abstract void terminate();

    /**
     * add a new goal for composite goal
     */
    public void addSubgoal(Goal g) {
        throw new RuntimeException("atomic goal could not have subgoals");
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
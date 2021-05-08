package com.bham.bc.entity.ai.behavior;

public class Transition{

    /**
     * List of actions to be taken during the transition
     */
    private final Action[] actions;
    /**
     * The destination of the state machine after the transition
     */
    private final State targetState;
    /**
     * The condition that causes the transition to occur
     */
    private final Condition condition;

    /**
     * Default constructor for a transition
     * @param actions List of actions to be taken during the transition
     * @param targetState The destination of the state machine after the transition
     * @param condition The condition that causes the transition to occur
     */
    public Transition(Action[] actions, State targetState, Condition condition){
        this.actions = actions;
        this.targetState = targetState;
        this.condition = condition;
    }

    /**
     *  Alternate constructor where there are no transition actions
     * @param targetState The destination of the state machine after the transition
     * @param condition The condition that causes the transition to occur
     */
    public Transition(State targetState, Condition condition){
        this.actions = null;
        this.targetState = targetState;
        this.condition = condition;
    }

    /**
     * Determines if the conditions for the transitions have been met
     * @return True is conditions have been met, False if not
     */
    public boolean isTriggered(){
        return condition.test();
    }

    /**
     * Returns the state which will be switched to after the transitions
     * @return The target state
     */
    public State getTargetState(){
        return targetState;
    }

    /**
     * Returns the actions that need to occur during the transition
     * @return list of transition actions
     */
    public Action[] getActions(){
        return actions;
    }
}

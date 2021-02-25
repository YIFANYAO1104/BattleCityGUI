package com.bham.bc.entity.ai;

/**
 * Base interface for Transitions
 */
interface Transition {
    /**
     * Determines if the conditions for the transitions have been met
     * @return True is conditions have been met, False if not
     */
    public boolean isTriggered();

    /**
     * Returns the state which will be switched to after the transitions
     * @return The target state
     */
    public State getTargetState();

    /**
     * Returns the actions that need to occur during the transition
     * @return list of transition actions
     */
    public Action[] getActions();
}

package com.bham.bd.entity.ai.behavior;

import java.util.Objects;
import java.util.stream.Stream;

public class StateMachine {
    /**
     * he beginning state of a machine, which is also its current state
     */
    protected State initialState;
    protected State currentState;

    /**
     * Constructor for a state machine
     * @param initialState The beginning state of a machine, which is also its current state
     */
    public StateMachine(State initialState){
        this.initialState = initialState;
        this.currentState = initialState;
    }

    /**
     * Updates the state machine, checks if it needs to transition.
     * If so, it will transition to the new state and handle the transition.
     * If not, it will just perform the actions of the state
     * @return The array of actions to be performed
     */
    public Action[] update(){
        Transition triggered = null;

        // Checks each transition of the current state and check if they are met
        for (Transition transition : currentState.getTransitions()){
            if (transition.isTriggered()){
                triggered = transition;
                break;
            }
        }

        if (triggered == null){
            return currentState.getActions();
        } else {
            State targetState = triggered.getTargetState();

            Action[] actions = concatActions(currentState.getExitActions(), triggered.getActions(), targetState.getEntryActions());

            currentState = targetState;
            return actions;
        }
    }

    /**
     * Helper function to concatenate the 3 action arrays
     * @param actions entry, transition and exit action arrays to be concatenated
     * @return a singular array consisting of all 3 input arrays
     */
    private Action[] concatActions(Action[]... actions) {
        return Stream.of(actions).filter(Objects::nonNull).flatMap(Stream::of).toArray(Action[]::new);
    }
}

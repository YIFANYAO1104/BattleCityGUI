package com.bham.bc.entity.ai;

import com.bham.bc.components.characters.Tank;

import java.util.Arrays;
import java.util.stream.Stream;

public class StateMachine {
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
     * @param exit the actions taken exiting a state
     * @param trans the actions taken during a transition
     * @param entry the actions taken on entry of a new state
     * @return a singular array consisting of all 3 input arrays
     */
    private Action[] concatActions(Action[] exit, Action[] trans, Action[] entry){
        Action[] exitPlusTrans = Stream.concat(Arrays.stream(exit), Arrays.stream(trans))
                .toArray(Action[]::new);

        return Stream.concat(Arrays.stream(exitPlusTrans), Arrays.stream(entry))
                .toArray(Action[]::new);
    }
}

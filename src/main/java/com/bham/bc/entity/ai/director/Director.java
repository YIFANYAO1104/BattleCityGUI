package com.bham.bc.entity.ai.director;

import com.bham.bc.entity.ai.*;
import java.util.Arrays;

public class Director {

    private final StateMachine stateMachine;
    private final int STATETIMELENGTH; // The base length at which each state lasts in the game
    private int stateTimeModifier; // The time at which states are increased/decreased. This value is incremented each loop of the FSM
    private BooleanCondition stateTimeLimit;
    private BooleanCondition playerStressLimit;
    private OrCondition endPeak;

    public Director(){
        this.STATETIMELENGTH = 60; //TODO: FIGURE OUT IF THIS IS A GOOD TIME
        this.stateTimeModifier = 0;
        stateMachine = createFSM();
    }

    private StateMachine createFSM(){
        // Create States of the Finite State Machine
        State buildUpState = new State(new Action[]{Action.BUILDUP}, null);
        State peakState = new State(new Action[]{Action.PEAK}, null);
        State relaxState = new State(new Action[]{Action.RELAX}, null);

        // Define all conditions for the State Machine
        stateTimeLimit = new BooleanCondition();
        playerStressLimit = new BooleanCondition();
        endPeak = new OrCondition(playerStressLimit, stateTimeLimit);

        // Create Transitions of the Finite State Machine
        Transition buildUpToPeak = new Transition(peakState, stateTimeLimit);
        Transition peakToRelax = new Transition(relaxState, endPeak);
        Transition relaxToBuildUp = new Transition(new Action[]{ Action.INCREMENTLOOP }, buildUpState, stateTimeLimit);

        // Set the created transitions
        buildUpState.setTransitions(new Transition[]{ buildUpToPeak });
        peakState.setTransitions(new Transition[]{ peakToRelax });
        relaxState.setTransitions(new Transition[]{ relaxToBuildUp });

        return new StateMachine(buildUpState);
    }

    public void update(){

        //TODO: stateTimeLimit.setTestValue(checkTime());
        //TODO: playerStressLimit.setTestValue(checkPlayerStressLimit());

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case BUILDUP:
                    // TODO: buildUp()
                    break;
                case PEAK:
                    // TODO: peak();
                    break;
                case RELAX:
                    // TODO: relax();
                    break;
                case INCREMENTLOOP:
                    // TODO: incrementTimeModifier();
                    break;
            }
        });
    }
}

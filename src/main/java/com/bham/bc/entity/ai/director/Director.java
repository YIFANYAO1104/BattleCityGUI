package com.bham.bc.entity.ai.director;

import com.bham.bc.entity.ai.behavior.*;
import java.util.Arrays;

public class Director {

    private final StateMachine stateMachine; // The State Machine that the director uses
    private final int STATETIMELENGTH = 30; // The base length at which each state lasts in the game
    private final int MAXSTATETIMEMOD = 20; // The maximum change in the length of each state
    private int stateTimeModifier; // The time at which states are increased/decreased. This value is incremented each loop of the FSM
    private BooleanCondition stateTimeLimit;
    private BooleanCondition playerStressLimit;
    private OrCondition endPeak;

    /**
     * Constructor for the Director. It sets the inital values and generates the FSM that the Director will use.
     */
    public Director(){
        this.stateTimeModifier = 0;
        stateMachine = createFSM();
    }

    /**
     * Creates the Finite State Machine for the Director AI,
     * Generates all the states and the transitions and keeps track of the conditions
     * @return the finished State Machine
     */
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

    /**
     * Updates the Director AI for each step of the game.
     * It updates each condition and finds which functions must be performed as according to the State Machine
     */
    public void update(){

        stateTimeLimit.setTestValue(checkTime());
        playerStressLimit.setTestValue(checkPlayerStressLimit());

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case BUILDUP:
                    buildUp();
                    break;
                case PEAK:
                    peak();
                    break;
                case RELAX:
                    relax();
                    break;
                case INCREMENTLOOP:
                    incrementTimeModifier();
                    break;
            }
        });
    }

    /**
     * The Build Up state should slowly increase the threat and difficulty for the player.
     * It does this by spawning more enemies, or spawning stronger enemies
     */
    private void buildUp(){

    }

    /**
     * The Peak state is when no more enemies are spawned as the player has reached their limit
     * The player needs to clean up the remaining enemies that are left on the screen
     */
    private void peak(){

    }

    /**
     * The relax state is where only a small steady amount of enemies is spawned, to give the player a break.
     * The time of this state will decrease over time to give the player less of a break
     */
    private void relax(){

    }

    /**
     * Increases the time modifier up to a limit
     */
    private void incrementTimeModifier(){
        if (this.stateTimeModifier < this.MAXSTATETIMEMOD){
            this.stateTimeModifier += 4;
        }
    }

    /**
     * Checks if the time limit for a state has been reached
     * @return
     */
    private boolean checkTime(){
        return false;
    }

    /**
     * Checks if the player has reached their stress limit
     * @return
     */
    private boolean checkPlayerStressLimit(){
        return false;
    }
}

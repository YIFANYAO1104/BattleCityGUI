package com.bham.bc.entity.ai.director;

//import com.bham.bc.components.FrontendServices;
import com.bham.bc.entity.ai.behavior.*;
import static com.bham.bc.utils.Timer.CLOCK;

import java.util.Arrays;

public class Director {

    private final StateMachine stateMachine; // The State Machine that the director uses
    private final int STATETIMELENGTH = 30; // The base length at which each state lasts in the game
    private final int MAXSTATETIMEMOD = 30; // The maximum change in the length of each state
    private int stateTimeModifier; // The time at which states are increased/decreased. This value is incremented each loop of the FSM
    private BooleanCondition stateTimeLimitUp; // Condition for counting a state's time limit that gets longer as the game goes on
    private BooleanCondition stateTimeLimitDown; // Condition for counting a state's time limit that gets shorter as the game goes on
    private BooleanCondition playerStressLimit;
    private OrCondition endBuildUp;
    private int enemyCount;
    private double playerHP; // Holds the player's health from the last check
    private double homeHP; // Holds the home's health from the last check
    private long lastTimeChecked = -1; // Holds the time in which the director last checked the game state
    private long stateTime = -1; // Holds the start time of a state in the state machine

    /**
     * Constructor for the Director. It sets the initial values and generates the FSM that the Director will use.
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
        stateTimeLimitUp = new BooleanCondition();
        stateTimeLimitDown = new BooleanCondition();
        playerStressLimit = new BooleanCondition();
        endBuildUp = new OrCondition(playerStressLimit, stateTimeLimitUp);

        // Create Transitions of the Finite State Machine
        Transition buildUpToPeak = new Transition(new Action[]{ Action.RESETTIMELIMIT },peakState, endBuildUp);
        Transition peakToRelax = new Transition(new Action[]{ Action.RESETTIMELIMIT },relaxState, stateTimeLimitDown);
        Transition relaxToBuildUp = new Transition(new Action[]{ Action.INCREMENTLOOP, Action.RESETTIMELIMIT }, buildUpState, stateTimeLimitDown);

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

        stateTimeLimitUp.setTestValue(checkTimeUp());
        stateTimeLimitDown.setTestValue(checkTimeDown());
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
                case RESETTIMELIMIT:
                    resetTimeLimit();
                    break;
            }
        });
    }

    /**
     * The Build Up state should slowly increase the threat and difficulty for the player.
     * Will check if 5 seconds has occurred since the last time an enemy has spawned
     * If so, it'll decide how many to spawn to push the player or give them a break
     * The standard enemy wave consists of 2 enemies. This will occur if the player kills no enemies, and takes little damage
     * If the player or home takes significant damage in the last 5 seconds, and no enemies are killed, then only 1 enemy is spawned.
     * If the player kills enemies in the last 5 seconds, the number of enemies spawned will increase such that the enemyCount
     * always increases by at least 1. For example, if the player kills 2 enemies in the last 5 seconds, then 3 enemies will be spawned in this wave
     */
    private void buildUp(){
        // Checks if it's been 5 seconds since an enemy has spawned, or checks if lastEnemySpawn is -1 which indictaes this the first time an enemy is spawned
        if (lastTimeChecked == -1 || (CLOCK.getCurrentTime() - lastTimeChecked) >= 5){
            lastTimeChecked = CLOCK.getCurrentTime();

            //TODO int newEnemyCount = getEnemyCount();
            int newEnemyCount = 0; //TEMPORARY
            int changeInEnemy = newEnemyCount - enemyCount;
            int numOfEnemies2Spawn = 0;
            //TODO double newPlayerHP = getPlayerHealth();
            double newPlayerHP = 50; //TEMPORARY
            double changeInPlayerHP = newPlayerHP - playerHP;
            //TODO double newHomeHP = getHomeHealth();
            double newHomeHP = 500; //TEMPORARY
            double changeInHomeHP = newHomeHP - homeHP;
            double percentageChangeInHealth = (changeInPlayerHP / 100 + changeInHomeHP / 1000) * 50;

            spawnEnemies(findNumEnemies2Spawn(changeInEnemy, percentageChangeInHealth));

            // TODO: enemyCount = getEnemyCount();
            // TODO: playerHP = getPlayerHealth();
            // TODO: homeHP = getHomeHealth();
        }

    }

    /**
     * Helper function to help make the buildUp function cleaner
     * @param changeInEnemy The change in the number of enemies in the last 5 seconds
     * @param percentageChangeInHealth The percentage change of both player and the home base in the last 5 seconds
     */
    private int findNumEnemies2Spawn(int changeInEnemy, double percentageChangeInHealth){
        int numOfEnemies2Spawn;
        if (changeInEnemy <= 0){
            //Case where the player has killed no enemies
            if (percentageChangeInHealth <= 10){
                // Case where the player hasn't taken much or at all any damage
                // So that the player does not get any lee-way
                numOfEnemies2Spawn = 2;
            } else if (percentageChangeInHealth <= 30) {
                // Case where the player has taken some damage so the number of enemies spawned is reduced
                numOfEnemies2Spawn = 1;
            } else{
                // Case where the player has taken a lot of damage so that no enemies are spawned
                numOfEnemies2Spawn = 0;
                //TODO: spawnPowerups();
            }
        } else {
            numOfEnemies2Spawn = changeInEnemy + 1;
        }
        return numOfEnemies2Spawn;
    }

    private void spawnEnemies(int number2BeSpawned){
        for (int i = 0; i < number2BeSpawned; i++){
            //TODO spawnEnemy();
        }
    }

    /**
     * The Peak state is when no more enemies are spawned as the player has reached their limit
     * The player needs to clean up the remaining enemies that are left on the screen
     * In this state the Director may spawn helpful powerups for the player if they are struggling
     */
    private void peak(){

        if (lastTimeChecked == -1 || (CLOCK.getCurrentTime() - lastTimeChecked) >= 10) {
            lastTimeChecked = CLOCK.getCurrentTime();

            //TODO double newPlayerHP = getPlayerHealth();
            double newPlayerHP = 50; //TEMPORARY
            double changeInPlayerHP = newPlayerHP - playerHP;
            //TODO double newHomeHP = getHomeHealth();
            double newHomeHP = 500; //TEMPORARY
            double changeInHomeHP = newHomeHP - homeHP;
            double percentageChangeInHealth = (changeInPlayerHP / 100 + changeInHomeHP / 1000) * 50;

            if (percentageChangeInHealth >= 50){
                //TODO: spawnPowerups();
            }
        }
    }

    /**
     * The relax state is where only a small steady amount of enemies is spawned, to give the player a break.
     * The time of this state will decrease over time to give the player less of a break
     * During this state the game is much more lenient. It will spawn not many enemies
     * And will spawn powerups if the player is still struggling
     */
    private void relax() {

        if (lastTimeChecked == -1 || (CLOCK.getCurrentTime() - lastTimeChecked) >= 10) {
            lastTimeChecked = CLOCK.getCurrentTime();

            int numOfEnemies2Spawn = 1;
            //TODO double newPlayerHP = getPlayerHealth();
            double newPlayerHP = 50; //TEMPORARY
            double changeInPlayerHP = newPlayerHP - playerHP;
            //TODO double newHomeHP = getHomeHealth();
            double newHomeHP = 500; //TEMPORARY
            double changeInHomeHP = newHomeHP - homeHP;
            double percentageChangeInHealth = (changeInPlayerHP / 100 + changeInHomeHP / 1000) * 50;

            // Ensures the player isn't struggling too much and gives help if so
            if (percentageChangeInHealth >= 50) {
                //TODO: spawnPowerups();
                numOfEnemies2Spawn = 0;
            }

            spawnEnemies(numOfEnemies2Spawn);

        }
    }

    /**
     * Increases the time modifier up to a limit
     */
    private void incrementTimeModifier(){
        if (this.stateTimeModifier < this.MAXSTATETIMEMOD){
            this.stateTimeModifier += 5;
        }
    }

    /**
     * Resets the time limit for a state
     */
    private void resetTimeLimit(){
        stateTime = CLOCK.getCurrentTime();
    }

    /**
     * Checks if the time limit for a state has been reached
     * @return
     */
    private boolean checkTimeUp(){
        if (stateTime == -1){ // Case where the stateTime has not been set
            stateTime = CLOCK.getCurrentTime();
            return false; }
        if ((CLOCK.getCurrentTime() - stateTime) >= (STATETIMELENGTH + stateTimeModifier)){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkTimeDown(){
        if (stateTime == -1){ // Case where the stateTime has not been set
            stateTime = CLOCK.getCurrentTime();
            return false;
        }
        if ((CLOCK.getCurrentTime() - stateTime) >= (STATETIMELENGTH - stateTimeModifier)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player has reached their stress limit
     * This calculated by calculating the damage taken by the player and home in the last 5 seconds
     * @return
     */
    private boolean checkPlayerStressLimit(){

        //double newPlayerHP = FrontendServices.getPlayerHP();
        double newPlayerHP = 50; //TEMPORARY
        double changeInPlayerHP = newPlayerHP - playerHP;
        //TODO double newHomeHP = getHomeHealth();
        double newHomeHP = 500; //TEMPORARY
        double changeInHomeHP = newHomeHP - homeHP;
        double percentageChangeInHealth = (changeInPlayerHP / 100 + changeInHomeHP / 1000) * 50;

        if (percentageChangeInHealth >= 50){
            return true;
        } else {
            return false;
        }
    }
}

package com.bham.bc.entity.ai.director;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.characters.agents.enemies.EnemyType;
import com.bham.bc.components.triggers.TriggerType;
import com.bham.bc.entity.ai.behavior.*;

import java.util.Arrays;
import java.util.Random;
import java.lang.Math;

import static com.bham.bc.utils.Timer.CLOCK;
import static com.bham.bc.components.Controller.services;

public class Director {

    /**
     * The State Machine that the director uses
     */
    private final StateMachine stateMachine;
    /**
     * The base length at which each state lasts in the game
     */
    private final int STATETIMELENGTH = 30000;
    /**
     * The maximum change in the length of each state
     */
    private final int MAXSTATETIMEMOD = 30000;
    /**
     * The time at which states are increased/decreased. This value is incremented each loop of the FSM
     */
    private int stateTimeModifier;
    /**
     * Condition for counting a state's time limit that gets longer as the game goes on
     */
    private BooleanCondition stateTimeLimitUp;
    /**
     * Condition for counting a state's time limit that gets shorter as the game goes on
     */
    private BooleanCondition stateTimeLimitDown;
    private BooleanCondition playerStressLimit;
    private OrCondition endBuildUp;
    private int enemyCount;
    /**
     * Holds the player's health from the last check
     */
    private double allyHpFraction;
    /**
     * Holds the home's health from the last check
     */
    private double homeHpFraction;
    /**
     * Holds the time in which the director last checked the game state
     */
    private long lastTick;
    /**
     * Holds the start time of a state in the state machine
     */
    private long stateTime;

    /**
     * Constructor for the Director. It sets the initial values and generates the FSM that the Director will use.
     */
    public Director(){
        this.stateTimeModifier = 0;
        stateMachine = createFSM();

        enemyCount = 0;
        allyHpFraction = 1;
        homeHpFraction = 1;
        lastTick = 0;
        stateTime = -1;
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
        Transition buildUpToPeak = new Transition(new Action[]{ Action.RESETTIMELIMIT, Action.SPAWNPOWERUPS, Action.SPAWNALLY },peakState, endBuildUp);
        Transition peakToRelax = new Transition(new Action[]{ Action.INCREMENTLOOP, Action.RESETTIMELIMIT, Action.SPAWNPOWERUPS, Action.SPAWNALLY },relaxState, stateTimeLimitDown);
        Transition relaxToBuildUp = new Transition(new Action[]{ Action.RESETTIMELIMIT, Action.SPAWNPOWERUPS, Action.SPAWNALLY }, buildUpState, stateTimeLimitDown);

        // Set the created transitions
        buildUpState.setTransitions(new Transition[]{ buildUpToPeak });
        peakState.setTransitions(new Transition[]{ peakToRelax });
        relaxState.setTransitions(new Transition[]{ relaxToBuildUp });

        return new StateMachine(relaxState);
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
                case SPAWNPOWERUPS:
                    spawnRandomPowerups(2);
                    break;
                case SPAWNALLY:
                    spawnAllies(1);
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
    private void buildUp() {
        // Checks if it's been 5 seconds since an enemy has spawned
        if(CLOCK.getCurrentTime() - lastTick >= 5000) {
            System.out.println("I am in Build state");
            lastTick = CLOCK.getCurrentTime();

            // Calculate change in enemy count
            int newEnemyCount = services.getCharacters(Side.ENEMY).size();
            int changeInEnemyCount = newEnemyCount - enemyCount;
            System.out.println("change in enemy: " + changeInEnemyCount);

            // Calculate change in ally hp fraction
            double newAllyHpFraction = services.getCharacters(Side.ALLY).stream().map(c -> c.getHp() / c.getFullHp()).reduce(0.0, Double::sum) / services.getCharacters(Side.ALLY).size();
            double changeInAllyHpFraction = newAllyHpFraction - allyHpFraction;

            // Calculate change in home hp fraction
            double newHomeHpFraction = services.getHomeHpFraction();
            double changeInHomeHpFraction = newHomeHpFraction - homeHpFraction;

            // Spawn the calculated number of enemies
            spawnRandomEnemies(findNumEnemies2Spawn(changeInEnemyCount, (changeInAllyHpFraction + changeInHomeHpFraction) * .5));

            // Update the stored values
            enemyCount = newEnemyCount;
            allyHpFraction = newAllyHpFraction;
            homeHpFraction = newHomeHpFraction;
        }
    }

    /**
     * Helper function to help make the buildUp function cleaner
     * @param changeInEnemy The change in the number of enemies in the last 5 seconds
     * @param percentageChangeInHealth The percentage change of both player and the home base in the last 5 seconds
     */
    private int findNumEnemies2Spawn(int changeInEnemy, double percentageChangeInHealth){
        int numOfEnemies2Spawn;
        if (changeInEnemy >= 0){
            //Case where the number of enemies has stayed the same or increased
            // meaning the player hasn't killed enough enemies to stay on top
            if (percentageChangeInHealth >= -.05) {
                // Case where the player has taken a small amount of damage so the number of enemies isn't reduced
                numOfEnemies2Spawn = 2;
            } else if (percentageChangeInHealth >= -.3) {
                // Case where the player has taken some damage so the number of enemies spawned is reduced
                numOfEnemies2Spawn = 1;
            } else{
                // Case where the player has taken a lot of damage so that no enemies are spawned
                numOfEnemies2Spawn = 0;
                spawnRandomPowerups(1);
            }
        } else {
            System.out.println("spawning else");
            numOfEnemies2Spawn = Math.abs(changeInEnemy) + 2;
        }
        return numOfEnemies2Spawn;
    }

    /**
     * Spawns a provided number of random powerups defined in {@link TriggerType}
     * @param numPowerupsToSpawn amount of powerups to be spawned
     */
    private void spawnRandomPowerups(int numPowerupsToSpawn) {
        System.out.println("Spawning " + numPowerupsToSpawn + " Powerups");

        TriggerType[] powerupTypes = Arrays.stream(TriggerType.values()).filter(t -> t.GROUP == TriggerType.TriggerGroup.POWERUP).toArray(TriggerType[]::new);
        Player player = services.getPlayer();
        if(player == null) return;

        for(int i = 0; i < numPowerupsToSpawn; i++) {
            int randomI = new Random().nextInt(powerupTypes.length);
            services.spawnTriggerAroundPoint(powerupTypes[randomI], player.getCenterPosition(), player.getHitBoxRadius(), 200);
        }
    }

    /**
     * Spawns a provided number of random enemies defined in {@link EnemyType}
     * @param numEnemiesToSpawn amount of enemies to be spawned
     */
    private void spawnRandomEnemies(int numEnemiesToSpawn) {
        System.out.println("Spawning " + numEnemiesToSpawn + " Enemies");
        for(int i = 0; i < numEnemiesToSpawn; i++) {
            int randomI = new Random().nextInt(EnemyType.values().length);
            services.spawnEnemyRandomly(EnemyType.values()[randomI]);
        }
    }

    /**
     * Spawns a provided number of random allies defined in {@link Neuron}
     * If the number of allies is less than 2, then no allies will be spawned
     * @param numAlliesToSpawn amount of enemies to be spawned
     */
    private void spawnAllies(int numAlliesToSpawn) {
        if (services.getCharacters(Side.ALLY).size() < 3){ // Checks if the number of allies is less than 3, which includes 1 player, so ensures a max of 2 neurons to be alive at one time
            System.out.println("Spawning " + numAlliesToSpawn + " Enemies");
            for(int i = 0; i < numAlliesToSpawn; i++) {
                services.spawnAlly();
            }
        }
        else {
            return; // Case where no allies are spawned because there are already enough
        }
    }

    /**
     * The Peak state is when no more enemies are spawned as the player has reached their limit
     * The player needs to clean up the remaining enemies that are left on the screen
     * In this state the Director may spawn helpful powerups for the player if they are struggling
     */
    private void peak(){
        if (lastTick == -1 || (CLOCK.getCurrentTime() - lastTick) >= 10000) {
            System.out.println("I am in the Peak state");
            lastTick = CLOCK.getCurrentTime();

            // Calculate change in ally hp fraction
            double newAllyHpFraction = services.getCharacters(Side.ALLY).stream().map(c -> c.getHp() / c.getFullHp()).reduce(0.0, Double::sum) / services.getCharacters(Side.ALLY).size();
            double changeInAllyHpFraction = newAllyHpFraction - allyHpFraction;

            // Calculate change in home hp fraction
            double newHomeHpFraction = services.getHomeHpFraction();
            double changeInHomeHpFraction = newHomeHpFraction - homeHpFraction;

            if((changeInAllyHpFraction + changeInHomeHpFraction) * .5 >= .5){
                spawnRandomPowerups(1);
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
        if(CLOCK.getCurrentTime() - lastTick >= 5000) {
            lastTick = CLOCK.getCurrentTime();
            System.out.println("I am in relax state");

            // Calculate change in ally hp fraction
            double newAllyHpFraction = services.getCharacters(Side.ALLY).stream().map(c -> c.getHp() / c.getFullHp()).reduce(0.0, Double::sum) / services.getCharacters(Side.ALLY).size();
            double changeInAllyHpFraction = newAllyHpFraction - allyHpFraction;

            // Calculate change in home hp fraction
            double newHomeHpFraction = services.getHomeHpFraction();
            double changeInHomeHpFraction = newHomeHpFraction - homeHpFraction;

            // Ensures the player isn't struggling too much and gives help if so
            if ((changeInAllyHpFraction + changeInHomeHpFraction) * .5 >= .5) {
                spawnRandomPowerups(1);
            } else {
                spawnRandomEnemies(1);
            }
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
    private boolean checkTimeUp() {
        if (stateTime == -1) { // Case where the stateTime has not been set
            stateTime = CLOCK.getCurrentTime();
            return false;
        }
        return CLOCK.getCurrentTime() - stateTime >= STATETIMELENGTH + stateTimeModifier;
    }

    private boolean checkTimeDown() {
        if (stateTime == -1) { // Case where the stateTime has not been set
            stateTime = CLOCK.getCurrentTime();
            return false;
        }

        return CLOCK.getCurrentTime() - stateTime >= STATETIMELENGTH - stateTimeModifier;
    }

    /**
     * Checks if the player has reached their stress limit
     * This calculated by calculating the damage taken by the player and home in the last 5 seconds
     * @return
     */
    private boolean checkPlayerStressLimit() {

        //double newPlayerHP = FrontendServices.getPlayerHP();
        // Calculate change in ally hp fraction
        double newAllyHpFraction = services.getCharacters(Side.ALLY).stream().map(c -> c.getHp() / c.getFullHp()).reduce(0.0, Double::sum) / services.getCharacters(Side.ALLY).size();
        double changeInAllyHpFraction = newAllyHpFraction - allyHpFraction;

        // Calculate change in home hp fraction
        double newHomeHpFraction = services.getHomeHpFraction();
        double changeInHomeHpFraction = newHomeHpFraction - homeHpFraction;

        return (changeInAllyHpFraction + changeInHomeHpFraction) * .5 >= .5;
    }
}

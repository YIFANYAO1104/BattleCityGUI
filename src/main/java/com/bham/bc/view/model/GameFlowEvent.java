package com.bham.bc.view.model;

import com.bham.bc.components.environment.MapType;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * <h1>Events for game flow</h1>
 *
 * <p>Represents event that is fired when a new instance of {@link com.bham.bc.view.GameSession} is created or when the user wants to
 * pause or leave the game session.</p>
 *
 * <p><b>Note:</b> this is a compact class for the whole game flow therefore different values are used by different event types. For
 * example, <i>PAUSE_GAME</i> could set properties like <i>numPlayers</i> but they would not be used by the current design of our
 * pause menu.</p>
 */
public class GameFlowEvent extends Event {
    public static final EventType<GameFlowEvent> START_GAME = new EventType<>(Event.ANY, "START_GAME");
    public static final EventType<GameFlowEvent> PAUSE_GAME = new EventType<>(Event.ANY, "PAUSE_GAME");
    public static final EventType<GameFlowEvent> LEAVE_GAME = new EventType<>(Event.ANY, "LEAVE_GAME");

    private MapType mapType;
    private int numPlayers;
    private boolean leaveGame;
    private double score;
    private String name;

    /**
     * Constructs a default event with 1 player, default map and
     *
     * @param eventType type of event we want to create - only START_GAME will be passed
     */
    public GameFlowEvent(EventType<? extends Event> eventType) {
        super(eventType);
        mapType = MapType.Map1;
        numPlayers = 1;
        leaveGame = false;
        score = -1;
        name = "";
    }

    /**
     * Sets the type of map
     * @param mapType EmptyMap or Map1 type of map the game should be initialized with
     */
    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    /**
     * Sets the number of players
     * @param numPlayers 1 or 2 active players the game should be initialized with
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = Math.max(1, Math.min(2, numPlayers));
    }

    /**
     * Sets the boolean value to indicate if the game session has ended
     * @param leaveGame {@code true} if the controller finished the game and {@code false} otherwise
     */
    public void setLeaveGame(boolean leaveGame) {
        this.leaveGame = leaveGame;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Gets the type of map that was set
     * @return enum EmptyMap or Map1 representing the type of map the game should be initialized with
     */
    public MapType getMapType() {
        return mapType;
    }

    /**
     * Gets the number of players the game should be initialized with
     * @return integer 1 or 2 representing active players the game should be initialized with
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Gets the boolean value showing if the game ended naturally (not aborted)
     * @return {@code true} if the controller finished the game and {@code false} otherwise
     */
    public boolean getLeaveGame() {
        return leaveGame;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }
}

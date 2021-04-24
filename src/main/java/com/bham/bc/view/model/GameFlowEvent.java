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
    private double score;
    private String name;

    /**
     * Constructs a default event with 1 player, default map, negative score and empty name
     *
     * @param eventType type of event we want to create - only START_GAME will be passed
     */
    public GameFlowEvent(EventType<? extends Event> eventType) {
        super(eventType);
        mapType = MapType.Map1;
        numPlayers = 1;
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
     * Sets the name of the player (joint name if 2 players) who was active during the game session
     * @param name name to potentially appear on the leaderboard
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the score which is achieved when the game ends
     * @param score end game score
     */
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
     * Gets the name of the player (joint name if 2 players) who was active during the game session
     * @return name to be put to the leaderboard
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the score that was achieved when the game ended
     * @return final game score or {@code -1} if the game session was aborted
     */
    public double getScore() {
        return score;
    }
}

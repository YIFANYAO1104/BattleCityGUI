package com.bham.bc.view.model;

import com.bham.bc.components.environment.MapType;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Represents event that is fired when a new instance of {@link com.bham.bc.view.GameSession} is created
 */
public class NewGameEvent extends Event {
    public static final EventType<NewGameEvent> START_GAME = new EventType<>(Event.ANY, "START_GAME");

    private MapType mapType;
    private int numPlayers;

    /**
     * Constructs a default event with for survival mode with 1 player and default map
     *
     * @param eventType type of event we want to create - only START_GAME will be passed
     */
    public NewGameEvent(EventType<? extends Event> eventType) {
        super(eventType);
        mapType = MapType.Map1;
        numPlayers = 1;
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
        this.numPlayers = numPlayers;
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
}

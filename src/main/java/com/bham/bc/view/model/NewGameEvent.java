package com.bham.bc.view.model;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import javafx.event.Event;
import javafx.event.EventType;

public class NewGameEvent extends Event {
    public static final EventType<NewGameEvent> START_GAME = new EventType<>(Event.ANY, "START_GAME");

    private MODE mode;
    private MapType mapType;
    private int numPlayers;

    public NewGameEvent(EventType<? extends Event> eventType) {
        super(eventType);
        mode = MODE.SURVIVAL;
        mapType = MapType.Map1;
        numPlayers = 1;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public MODE getMode() {
        return mode;
    }

    public MapType getMapType() {
        return mapType;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}

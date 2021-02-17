package com.bham.bc.components.environment;

import com.bham.bc.entity.BaseGameEntity;

public abstract class MapObject2D extends BaseGameEntity {
    /**
     * Constructor Of Object,genreating a BaseGameEntity ID
     * @param x
     * @param y
     */
    public MapObject2D(int x, int y) {
        super(GetNextValidID(),x,y);
    }

    @Override
    public String toString() {
        return "GameMap element";
    }
}

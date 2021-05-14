package com.bham.bd.components.environment;

/**
 * Represents a type of map that can be loaded by {@link MapLoader}
 */
public enum MapType {
    SMALL("model/maps/64x64.json"),
    MEDIUM("model/maps/86x86.json"),
    LARGE("model/maps/162x162.json"),
    TEST1("model/maps/floodMap1.json"),
    TEST2("model/maps/test.json"),
    S("surr.json"),
    Trigger162("Trigger162x162.json"),
    EMPTY(null);

    /** Path to JSON file from which a map can be constructed */
    public final String PATH;

    /**
     * Constructs an enum which defines the type of map
     * @param pathToMap path to JSON file from which a map can be constructed
     */
    MapType(String pathToMap) {
        PATH = pathToMap;
    }
}

package com.bham.bc.components.environment;

public enum MapType {

    SMALL("/model/maps/64x64.json"),
    MEDIUM("/model/maps/86x86.json"),
    LARGE("/model/maps/162x162.json"),
    TEST1("/model/maps/floodMap1.json"),
    TEST2("/model/maps/test.json"),
    S("/surr.json"),
    Trigger162("/Trigger162x162.json"),
    EMPTY(null);

    MapType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }
}

package com.bham.bc.components.environment;

public enum MapType {

    SMALL("/64x64.json"),
    MEDIUM("/86x86.json"),
    LARGE("/162x162.json"),
    TEST1("/com.bham.bc/floodMap1.json"),
    TEST2("/test.json"),
    EMPTY(null);

    MapType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }
}

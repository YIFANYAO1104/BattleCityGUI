package com.bham.bc.components.environment;

public enum MapType {

    Map1("/64x64.json"),
    TESTMap1("/com.bham.bc/floodMap1.json"),
    TEST("/test.json"),
    MAP2("/162x162.json"),
    EmptyMap(null);

    MapType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }
}

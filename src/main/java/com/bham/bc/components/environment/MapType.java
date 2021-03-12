package com.bham.bc.components.environment;

public enum MapType {

    Map1("/64x64.json"),
    TESTMap1("/floodMap1.json");

    MapType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }
}

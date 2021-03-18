package com.bham.bc.view.model;

public enum TankType {

    type1("file:src/main/resources/GUIResources/tank1U.gif"),
    type2("file:src/main/resources/GUIResources/tank2U.gif"),
    type3("file:src/main/resources/GUIResources/tank3U.gif");


    String urlShip;


    private TankType(String urlShip) {
        this.urlShip = urlShip;

    }

    public String getUrl() {
        return this.urlShip;
    }


}

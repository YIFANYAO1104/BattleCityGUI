package com.bham.bc.entity.ai.navigation;

/**
 * The item type an agent want to find
 * Being passed as an argument for NavigationService
 * Being determined via the{@link com.bham.bc.entity.graph.ExtraInfo} Interface
 */
public enum ItemType {
    HEALTH,
    WEAPON,
    ALLY,
    HOME,
    SOFT,
    ENEMY_AREA
}

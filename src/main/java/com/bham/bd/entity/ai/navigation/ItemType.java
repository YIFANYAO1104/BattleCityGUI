package com.bham.bd.entity.ai.navigation;

/**
 * The item type an agent want to find
 * Being passed as an argument for NavigationService
 * Being determined via the{@link com.bham.bd.entity.graph.ExtraInfo} Interface
 */
public enum ItemType {
    HEALTH,
    WEAPON,
    ALLY,
    HOME,
    SOFT,
    ENEMY_AREA
}

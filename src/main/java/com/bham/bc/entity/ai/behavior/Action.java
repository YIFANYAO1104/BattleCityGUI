package com.bham.bc.entity.ai.behavior;

/**
 * Actions are enums which correspond to behaviours that are programmed into the enemy classes
 */
public enum Action{
    // ENEMY

    // Continuous
    ATTACK_ALLY,
    ATTACK_HOME,
    ATTACK_OBST,
    SEARCH_ALLY,
    SEARCH_HOME,
    CHARGE_ALLY,
    RETREAT,
    REGENERATE,

    // Entry
    SET_RATE,
    SET_SPEED,
    SET_SEARCH,

    // Exit
    RESET_RATE,
    RESET_SPEED,
    RESET_SEARCH,

    // DIRECTOR
    BUILDUP,
    PEAK,
    RELAX,
    INCREMENTLOOP,
    RESETTIMELIMIT,
}
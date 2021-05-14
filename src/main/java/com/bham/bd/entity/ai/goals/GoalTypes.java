/*
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bd.entity.ai.goals;

/**
 * class to define all the goal types
 * including atomic and composite
 */
public class GoalTypes {
    public static final int brain = 0;
    public static final int explore = 1;
    public static final int seek_to_position = 2;
    public static final int follow_path = 3;
    public static final int follow_edge = 4;
    public static final int navigate_to_character = 5;
    public static final int get_health = 6;
    public static final int attack_closest_target = 7;
    public static final int wait_for_path = 8;
}
/*
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.entity.ai.goals;

/**
 * class to define all the goal types
 * including atomic and composite
 */
public class GoalTypes {

    public static final int goal_think = 0;
    public static final int goal_explore = 1;
    public static final int goal_seek_to_position = 2;
    public static final int goal_follow_path = 3;
    public static final int goal_traverse_edge = 4;
    public static final int goal_move_to_position = 5;
    public static final int goal_get_health = 6;
    public static final int goal_attack_closest_target = 7;
    public static final int goal_wait_for_path = 8;
}
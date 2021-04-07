package com.bham.bc.utils;

/**
 * Final Class for size of Window
 */
final public class Constants {
    final public static int WINDOW_WIDTH = 800;
    final public static int WINDOW_HEIGHT = 600;
    final public static int MAP_WIDTH = 64*16;
    final public static int MAP_HEIGHT = 64*16;
    final public static int FRAME_RATE = 24;

    final public static int SAMPLE_MOVINGENTITY_WIDTH = 32;
    final public static int SAMPLE_MOVINGENTITY_HEIGHT = 32;
    final public static int GRAPH_NUM_CELLS_X = MAP_WIDTH / (SAMPLE_MOVINGENTITY_WIDTH/2);
    final public static int GRAPH_NUM_CELLS_Y = MAP_HEIGHT / (SAMPLE_MOVINGENTITY_HEIGHT/2);
    final public static double HITBOX_RADIUS = Math.sqrt((SAMPLE_MOVINGENTITY_WIDTH/2.0)*(SAMPLE_MOVINGENTITY_WIDTH/2.0));
    final public static double GRAPH_GRAPH_OBSTACLE_EDGE_COST = 10000.0;

}

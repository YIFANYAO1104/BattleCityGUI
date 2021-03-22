package com.bham.bc.utils;

import com.bham.bc.components.characters.enemies.Enemy;

/**
 * Final Class for size of Window
 */
final public class Constants {
    final public static int TILE_WIDTH = 16;
    final public static int TILE_HEIGHT = 16;
    final public static int WINDOW_WIDTH = 800;
    final public static int WINDOW_HEIGHT = 600;
    final public static int MAP_WIDTH = 64*TILE_WIDTH;
    final public static int MAP_HEIGHT = 64*TILE_HEIGHT;
    final public static int FRAME_RATE = 24;

    final public static int GRAPH_NUM_CELLS_X = MAP_WIDTH / (Enemy.WIDTH/2);
    final public static int GRAPH_NUM_CELLS_Y = MAP_HEIGHT / (Enemy.HEIGHT/2);
    final public static double GRAPH_GRAPH_OBSTACLE_EDGE_COST = 10000.0;

}

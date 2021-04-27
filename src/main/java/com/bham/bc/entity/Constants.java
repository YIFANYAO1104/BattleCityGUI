package com.bham.bc.entity;

public class Constants {
    //CHARACTER
    /**
     *   Type       |  SPEED | HEALTH  |
     *   player     |  5     | 100     |
     *   Shooter    |  3     | 100     |
     *   Splitter   |  2     | 300     |
     *   Tank       |  2     | 300     |
     *   Teaser     |  5     | 100     |
     *   Trapper    |  3     | 100     |
     */
    final public static double MAX_CHARACTER_SPEED = 5;
    final public static double MAX_CHARACTER_HEALTH = 300;

    //BULLET
    /**
     *   Type              |  SPEED  | DAMAGE  |
     *   DefaultBullet     |  5      | 10      |
     *   ExplosiveBullet   |  ?      | 100     |
     *   IceBullet         |  5      | 10      |
     *   ThunderLaser      |  10     | 10      |
     */
    final public static double MAX_BULLET_SPEED = 10;
    final public static double MAX_BULLET_HURT = 100;

    //OBSTACLE
    final public static double MAX_OBSTACLE_HEALTH = 50;//50

    //MAP
    final public static double MAX_MAP_WIDTH = 128*16;
    final public static double MAX_MAP_HEIGHT = 128*16;

    //HOME HP
    final public static double MAX_HOME_HP = 1000;
    final public static double MAX_HOME_DAMAGE = 1;
}

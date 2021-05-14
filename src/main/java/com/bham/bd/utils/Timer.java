package com.bham.bd.utils;

public class Timer {

    public static final Timer CLOCK = new Timer();
    private final long START_TIME;

    /**
     * Constructs the sole timer for the entire game by setting up the start time
     */
    private Timer() {
        START_TIME = System.currentTimeMillis();
    }

    /**
     * Gets how much time has elapsed since the start time was set up
     * @return number indicating how many milliseconds have passed since the game was launched
     */
    public long getCurrentTime() {
        return System.currentTimeMillis() - START_TIME;
    }
}

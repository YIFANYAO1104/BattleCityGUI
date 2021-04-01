package com.bham.bc.utils.time;

public class CrudeTimer {

    public static final CrudeTimer CLOCK = new CrudeTimer();
    private final long START_TIME;

    /**
     * Constructs the sole timer for the entire game by setting up the start time
     */
    private CrudeTimer() {
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

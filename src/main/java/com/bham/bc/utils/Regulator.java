/**
 *  Desc:   Use this class to regulate code flow (for an update function say)
 *          Instantiate the class with the frequency you would like your code
 *          section to flow (like 10 times per second) and then only allow 
 *          the program flow to continue if Ready() returns true
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.utils;

public class Regulator {

    private static final double noise = 10.0;

    private double intervals;
    private long nextRegulateTime;

    public Regulator(double intervalsSecond) {

        assert (intervalsSecond >0) : "invalid interval";
        intervals = intervalsSecond*1000;
        nextRegulateTime = (long) (System.currentTimeMillis() + intervals);



//        if (updateTimePerSecond > 0) {
//            intervals = 1000.0 / updateTimePerSecond;
//        } else if (Math.abs(updateTimePerSecond)<1e-8) {
//            intervals = 0.0;
//        } else if (updateTimePerSecond < 0) {
//            intervals = -1;
//        }
    }


    /**
     * @return true if the current time exceeds m_dwNextUpdateTime
     */
    public boolean isReady() {
        //allow regulate periodically
        long currentTime = System.currentTimeMillis();

        if (currentTime >= nextRegulateTime) {
            nextRegulateTime = (long) (currentTime + intervals + RandomEnhanced.randDouble(-noise, noise));
            return true;
        }

        return false;
    }
}

package com.bham.bc.utils.time;

public class CrudeTimer {

    final public static CrudeTimer Clock = new CrudeTimer();
    //set to the time (in seconds) when class is instantiated
    private double m_dStartTime;

    //set the start time
    private CrudeTimer() {
        m_dStartTime = System.currentTimeMillis() * 0.001;
    }

    //copy ctor and assignment should be private
    private CrudeTimer(CrudeTimer c) {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public CrudeTimer Instance() {
        return Clock;
    }

    //returns how much time has elapsed since the timer was started
    public double GetCurrentTime() {
        return System.currentTimeMillis() * 0.001 - m_dStartTime;
    }
}

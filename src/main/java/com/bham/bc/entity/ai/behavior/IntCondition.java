package com.bham.bc.entity.ai.behavior;

/**
 * Conditions that require testing an integer within a certain threshold
 */
public class IntCondition implements Condition{
    /**
     * minimum value of the threshold
     */
    private final int minValue;
    /**
     * maximum value of the threshold
     */
    private final int maxValue;
    private int testValue;

    /**
     * Constructor for this condition
     * @param minValue minimum value of the threshold
     * @param maxValue maximum value of the threshold
     */
    public IntCondition(int minValue, int maxValue){
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Allows the test value to be reset to a new value ready to test
     * @param testValue value to be tested
     */
    public void setTestValue(int testValue){
        this.testValue = testValue;
    }

    /**
     * Tests if the test value is in between the min and max value
     * @return True if testValue is between the min and max value, False if not
     */
    public boolean test(){
        return (minValue <= testValue) && (testValue <= maxValue);
    }
}

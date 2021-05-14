package com.bham.bd.entity.ai.behavior;

public class BooleanCondition implements Condition{

    /**
     * value to be tested
     */
    private boolean testValue;

    /**
     * Constructs the BooleanCondition
     * @param testValue value to be tested
     */
    public BooleanCondition(boolean testValue){
        this.testValue = testValue;
    }

    /**
     * Constructor with no paramter so is auto-set to false
     */
    public BooleanCondition(){
        this.testValue = false;
    }

    /**
     * Allows the test value to be reset to a new value ready to test
     * @param testValue value to be tested
     */
    public void setTestValue(boolean testValue){
        this.testValue = testValue;
    }

    /**
     * Tests if the test value is in between the min and max value
     * @return True if testValue is between the min and max value, False if not
     */
    public boolean test(){
        return testValue;
    }
}

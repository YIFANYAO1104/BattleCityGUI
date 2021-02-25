package com.bham.bc.entity.ai;

/**
 * Condition for inverting the result of another Condition
 */
public class NotCondition {
    private final Condition conditionA;

    /**
     * Constructor
     * @param conditionA Condition to be inverted
     */
    public NotCondition(Condition conditionA){
        this.conditionA = conditionA;
    }

    /**
     * Inverts result of Condition's test
     * @return True if Condition is False, False if Condition is True
     */
    public boolean test(){
        return !conditionA.test();
    }
}
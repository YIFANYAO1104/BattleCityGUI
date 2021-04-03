package com.bham.bc.entity.ai.behavior;

/**
 * Condition for binding two Conditions in an AND statement
 */
public class AndCondition implements Condition{
    private final Condition conditionA;
    private final Condition conditionB;

    /**
     * Constructor
     * @param conditionA First Condition
     * @param conditionB Second Condition
     */
    public AndCondition(Condition conditionA, Condition conditionB){
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }

    /**
     * Tests if both conditions return True for their respective tests
     * @return True if both conditions return True, False if not
     */
    public boolean test(){
        return conditionA.test() && conditionB.test();
    }
}

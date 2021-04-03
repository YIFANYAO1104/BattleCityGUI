package com.bham.bc.entity.ai.behavior;

/**
 * Condition for binding two Conditions in an OR statement
 */
public class OrCondition implements Condition{
    private final Condition conditionA;
    private final Condition conditionB;

    /**
     * Constructor
     * @param conditionA First Condition
     * @param conditionB Second Condition
     */
    public OrCondition(Condition conditionA, Condition conditionB){
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }

    /**
     * Tests if either Condition returns True
     * @return True if 1 or both Conditions return True, False if not
     */
    public boolean test(){
        return conditionA.test() || conditionB.test();
    }
}

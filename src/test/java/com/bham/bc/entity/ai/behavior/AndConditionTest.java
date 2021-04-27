package com.bham.bc.entity.ai.behavior;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AndConditionTest {

    @Test
    public void testBothFalse(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        AndCondition andCondition = new AndCondition(condition1, condition2);

        condition1.setTestValue(false);
        condition2.setTestValue(false);

        assertFalse(andCondition.test());
    }

    @Test
    public void testBothTrue(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        AndCondition andCondition = new AndCondition(condition1, condition2);

        condition1.setTestValue(true);
        condition2.setTestValue(true);

        assertTrue(andCondition.test());
    }

    @Test
    public void testOneTrueOneFalse(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        AndCondition andCondition = new AndCondition(condition1, condition2);

        condition1.setTestValue(true);
        condition2.setTestValue(false);

        assertFalse(andCondition.test());
    }
}

package com.bham.bc.entity.ai.behavior;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrConditionTest {

    @Test
    public void testBothFalse(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        OrCondition andCondition = new OrCondition(condition1, condition2);

        condition1.setTestValue(false);
        condition2.setTestValue(false);

        assertFalse(andCondition.test());
    }

    @Test
    public void testBothTrue(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        OrCondition orCondition = new OrCondition(condition1, condition2);

        condition1.setTestValue(true);
        condition2.setTestValue(true);

        assertTrue(orCondition.test());
    }

    @Test
    public void testOneTrueOneFalse(){
        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();
        OrCondition orCondition = new OrCondition(condition1, condition2);

        condition1.setTestValue(true);
        condition2.setTestValue(false);

        assertTrue(orCondition.test());
    }
}

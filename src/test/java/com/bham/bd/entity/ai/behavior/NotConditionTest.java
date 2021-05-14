package com.bham.bd.entity.ai.behavior;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotConditionTest {

    @Test
    public void testFalse(){
        BooleanCondition condition1 = new BooleanCondition();
        NotCondition notCondition = new NotCondition(condition1);

        condition1.setTestValue(false);

        assertTrue(notCondition.test());
    }

    @Test
    public void testTrue(){
        BooleanCondition condition1 = new BooleanCondition();
        NotCondition notCondition = new NotCondition(condition1);

        condition1.setTestValue(true);

        assertFalse(notCondition.test());
    }
}

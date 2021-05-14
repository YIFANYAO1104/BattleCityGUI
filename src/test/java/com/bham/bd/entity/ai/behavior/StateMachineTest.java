package com.bham.bd.entity.ai.behavior;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class StateMachineTest {

    @Test
    public void testInitialState(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        assertTrue(((sm.currentState == state1) && (sm.initialState == state1)));
    }

    @Test
    public void testTransitionToState(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        condition1.setTestValue(true);

        sm.update(); // State machine should now have changed to state 2

        assertTrue((sm.currentState == state2));
    }

    @Test
    public void testActions(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        Action[] actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_ALLY));
    }

    @Test
    public void testMultipleActions(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY, Action.ATTACK_HOME}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        Action[] actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_ALLY && actions[1] == Action.ATTACK_HOME));
    }

    @Test
    public void testEntryActions(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        state2.setEntryActions(new Action[]{ Action.ATTACK_HOME});

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        condition1.setTestValue(true);

        Action[] actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_HOME));

        actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_OBST));
    }

    @Test
    public void testExitActions(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        state1.setExitActions(new Action[]{ Action.ATTACK_HOME});

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        condition1.setTestValue(true);

        Action[] actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_HOME));

        actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_OBST));
    }

    @Test
    public void testTransitionAction(){
        State state1 = new State(new Action[]{ Action.ATTACK_ALLY}, null);
        State state2 = new State(new Action[]{ Action.ATTACK_OBST}, null);

        BooleanCondition condition1 = new BooleanCondition();
        BooleanCondition condition2 = new BooleanCondition();

        Transition one2Two = new Transition(new Action[]{Action.ATTACK_HOME}, state2, condition1);
        Transition two2One = new Transition(state1, condition2);

        state1.setTransitions(new Transition[]{one2Two});
        state2.setTransitions(new Transition[]{two2One});

        StateMachine sm = new StateMachine(state1);

        condition1.setTestValue(true);

        Action[] actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_HOME));

        actions = sm.update();

        assertTrue((actions[0] == Action.ATTACK_OBST));    }
}

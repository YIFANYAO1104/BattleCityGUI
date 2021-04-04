package com.bham.bc.entity.ai.director;

import com.bham.bc.entity.ai.Action;
import com.bham.bc.entity.ai.StateMachine;

public class Director {

    private final StateMachine stateMachine;
    private int stateTimeLength;
    private int
    public Director(){
        stateMachine = createFSM();
    }

    private StateMachine createFSM(){
        State buildUpState = new State(new Action[]{Action.BUILDUP});
        State peakState = new State(new Action[]{Action.PEAK});
        State relaxState = new State(new Action[]{Action.RELAX});
        return
    }
}

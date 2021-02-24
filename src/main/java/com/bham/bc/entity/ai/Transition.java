package com.bham.bc.entity.ai;

interface Transition {
    public boolean isTriggered();
    public State getTargetState();
    public Action[] getActions();
}

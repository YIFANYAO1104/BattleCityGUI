package com.bham.bc.entity.ai;

interface State {
    public Action[] getActions();
    public Action[] getEntryActions();
    public Action[] getExitActions();
    public Transition[] getTransitions();
}

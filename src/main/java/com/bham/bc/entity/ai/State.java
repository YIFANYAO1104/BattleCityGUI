package com.bham.bc.entity.ai;

interface State {
    Action[] getActions();
    Action[] getEntryActions();
    Action[] getExitActions();
    Transition[] getTransitions();
}

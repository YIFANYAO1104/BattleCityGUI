package com.bham.bc.entity.ai.behavior;

public class State{

    /**
     * List of actions to be performed while the State is active
     */
    private Action[] actions;
    /**
     * List of actions to be performed when the State is entered
     */
    private Action[] entryActions;
    /**
     * List of actions to be performed when the State is exited
     */
    private Action[] exitActions;
    /**
     * List of transitions that can occur from this state
     */
    private Transition[] transitions;

    /**
     * Default constructor for a State
     * @param actions List of actions to be performed while the State is active
     * @param entryActions List of actions to be performed when the State is entered
     * @param exitActions List of actions to be performed when the State is exited
     * @param transitions List of transitions that can occur from this state
     */
    public State(Action[] actions, Action[] entryActions, Action[] exitActions, Transition[] transitions){
        this.actions = actions;
        this.entryActions = entryActions;
        this.exitActions = exitActions;
        this.transitions = transitions;
    }

    /**
     * Alternate constructor for a State where there are no entry or exit actions
     * @param actions List of actions to be performed while the State is active
     * @param transitions List of transitions that can occur from this state
     */
    public State(Action[] actions, Transition[] transitions){
        this.actions = actions;
        this.entryActions = null;
        this.exitActions = null;
        this.transitions = transitions;
    }

    /**
     * Alternate constructor to create an empty state which can be filled using setters later
     */
    public State(){
        this.actions = null;
        this.entryActions = null;
        this.exitActions = null;
        this.transitions = null;
    }

    /**
     * Setter for Transitions of this State
     * @param newTransitions The new Transitions
     */
    public void setTransitions(Transition[] newTransitions) { this.transitions = newTransitions;}

    /**
     * Setter for the entry actions of this State
     * @param newActions the new entry actions
     */
    public void setEntryActions(Action[] newActions){ this.entryActions = newActions;}

    /**
     * Setter for the exit actions of this State
     * @param newActions the new exit actions
     */
    public void setExitActions(Action[] newActions){ this.exitActions = newActions;}

    /**
     * Setter for the actions of this State
     * @param newActions the new actions
     */
    public void setActions(Action[] newActions){ this.actions = newActions;}

    /**
     * Returns the actions of the State
     * @return the actions
     */
    public Action[] getActions(){
        return actions;
    }

    /**
     * Returns the actions to start the State
     * @return The entry actions
     */
    public Action[] getEntryActions(){
        return entryActions;
    }

    /**
     * Returns the actions to leave the State with
     * @return The exit actions
     */
    public Action[] getExitActions(){
        return exitActions;
    }

    /**
     * Returns the transitions of the State
     * @return the transitions
     */
    public Transition[] getTransitions(){
        return transitions;
    }
}
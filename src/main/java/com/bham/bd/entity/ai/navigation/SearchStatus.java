package com.bham.bd.entity.ai.navigation;

/**
 * these enums are used as return values from each search update method
 */
public enum SearchStatus {
    /**
     * To indicate the a state when there been no searching task for {@link NavigationService}
     * This also includes the situation when there been no closest node around player or target
     */
    no_task,
    /**
     * To indicate the a state when search was not completed
     */
    search_incomplete,
    /**
     * To indicate the a state when search was completed
     */
    target_found,
    /**
     * To indicate the a state when search was completed
     */
    target_not_found;
}
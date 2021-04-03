package com.bham.bc.entity.ai.navigation;

public enum SearchStatus {

//these enums are used as return values from each search update method
    target_found,
    target_not_found,
    search_incomplete,
    no_task;
}
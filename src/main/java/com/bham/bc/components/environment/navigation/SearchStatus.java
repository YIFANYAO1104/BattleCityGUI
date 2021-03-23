/**
 * Desc: classes to implement graph algorithms that can be distributed over
 * multiple update-steps
 *
 * Any graphs passed to these functions must conform to the same interface used
 * by the SparseGraph
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.components.environment.navigation;

public enum SearchStatus {

//these enums are used as return values from each search update method
    target_found,
    target_not_found,
    search_incomplete;
}
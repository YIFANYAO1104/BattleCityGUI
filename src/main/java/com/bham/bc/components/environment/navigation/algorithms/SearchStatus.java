/**
 * Desc: classes to implement graph algorithms that can be distributed over
 * multiple update-steps
 *
 * Any graphs passed to these functions must conform to the same interface used
 * by the SparseGraph
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.components.environment.navigation.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final public class SearchStatus {

//these enums are used as return values from each search update method
    public static final int target_found = 0;
    public static final int target_not_found = 1;
    public static final int search_incomplete = 2;
}
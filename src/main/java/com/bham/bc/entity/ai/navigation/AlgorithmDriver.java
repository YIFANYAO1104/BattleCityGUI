/**
 * Desc: a template class to manage a number of graph searches, and to
 * distribute the calculation of each search over several update-steps
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.entity.ai.navigation;

import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Time Sliced Algorithm Runner
 */
public class AlgorithmDriver {

	/**
	 * a container of all the active search requests
	 */
	private List<PathPlanner> requests = new LinkedList<>();
	/**
	 * this is the total number of search cycles allocated to the manager. Each
	 * update-step these are divided equally amongst all registered path
	 * requests
	 */
	private int cycleTimesPerUpdate;

	/**
	 * constructor
	 * @param cycleTimesPerUpdate the maximum time steps the driver would run on each game update
	 */
	public AlgorithmDriver(int cycleTimesPerUpdate) {
		this.cycleTimesPerUpdate = cycleTimesPerUpdate;
	}


	/**
	 * execute cycleTimesPerUpdate steps searching algorithms
	 */
	public void runAlgorithm() {
		int remainCycles = cycleTimesPerUpdate;

		Iterator<PathPlanner> iterator = requests.iterator();
		while (remainCycles > 0 && !requests.isEmpty()) {
			PathPlanner temp = iterator.next();
			//make one search cycle of this path request
			SearchStatus result = temp.cycleOnce();

			//if complete
			if ((result == SearchStatus.target_found) || (result == SearchStatus.target_not_found)) {
				iterator.remove();
			}

			//iterate from the head if we still have more cycles
			if (!iterator.hasNext()) {
				iterator = requests.iterator();
			}
			remainCycles--;

		}//end while
	}

	/**
	 * a path planner should call this method to register a search with the
	 * manager. (The method checks to ensure the path planner is only registered
	 * once)
	 */
	public void register(PathPlanner p) {
		if (!requests.contains(p)) {
			requests.add(p);
		}
	}

	/**
	 * unregister a path planner from the driver
	 * @param p the path planner going to be unregistered
	 */
	public void unRegister(PathPlanner p) {
		requests.remove(p);
	}
}

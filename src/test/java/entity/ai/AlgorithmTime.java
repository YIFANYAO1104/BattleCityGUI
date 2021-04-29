package entity.ai;

import com.bham.bc.components.Controller;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.algorithms.TimeSlicedAlgorithm;
import com.bham.bc.entity.ai.navigation.algorithms.TimeSlicedDijkstras;
import com.bham.bc.entity.ai.navigation.algorithms.astar.TimeSlicedAStar;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.entity.ai.navigation.algorithms.policies.TerminationPolices;
import com.bham.bc.entity.graph.SparseGraph;
import javafx.embed.swing.JFXPanel;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class AlgorithmTime {

    private Object[] map1AStarParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        SparseGraph sg = Controller.services.getGraph();
        Object[][] typeListList = { //
                { 2016, 149  , sg},
                { 2016, 166  , sg},
                { 2016, 260  , sg},
                { 2016, 381  , sg},
                { 2016, 933  , sg},
                { 2016, 2233 , sg},
                { 2016, 2434 , sg},
                { 2016, 3679 , sg},
                { 2016, 3781 , sg},
                { 2016, 4028 , sg}
        };
        return typeListList;
    }

    private Object[] map1DijkstraParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        SparseGraph sg = Controller.services.getGraph();
        Object[][] typeListList = { //
                { 149  , sg},
                { 166  , sg},
                { 260  , sg},
                { 381  , sg},
                { 933  , sg},
                { 2233 , sg},
                { 2434 , sg},
                { 3679 , sg},
                { 3781 , sg},
                { 4028 , sg}
        };
        return typeListList;
    }

    @Test
    @Parameters(method = "map1AStarParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void AStarCase(int source,int target, SparseGraph sg){
        long startTime=System.currentTimeMillis();
        TimeSlicedAlgorithm task =new TimeSlicedAStar(sg,source,target,new ExpandPolicies.NoInvalid());
        while (task.cycleOnce() == SearchStatus.search_incomplete){

        }
        long endTime=System.currentTimeMillis();
        assertTrue(endTime-startTime < 15);
    }

    @Test
    @Parameters(method = "map1DijkstraParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void DijkstraCase(int source,SparseGraph sg){
        long startTime=System.currentTimeMillis();
        TimeSlicedAlgorithm task = new TimeSlicedDijkstras(sg,source, ItemType.HEALTH,
                new TerminationPolices.FindActiveTrigger(),
                new ExpandPolicies.NoInvalid());
        while (task.cycleOnce() == SearchStatus.search_incomplete){

        }
        long endTime=System.currentTimeMillis();
        assertTrue(endTime-startTime < 150);
    }
}

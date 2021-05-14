package com.bham.bd.entity.ai.navigation;

import com.bham.bd.components.Controller;
import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.components.characters.Player;
import com.bham.bd.components.environment.MapType;
import com.bham.bd.entity.ai.navigation.impl.PathPlanner;
import com.bham.bd.entity.graph.SparseGraph;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * test the service flow for path planner
 * no_task -> incomplete -> (targetfound or targetnotfound)
 */
@RunWith(JUnitParamsRunner.class)
public class NavigationFlowTest {

    private Object[] testParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(16*32, 16*32);
        SparseGraph sg = Controller.services.getGraph();

        Object[][] typeListList = { //
                { player, sg}
        };
        return typeListList;
    }

    @Test
    @Parameters(method = "testParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void normalFlow(GameCharacter player, SparseGraph sg) throws Exception {
        PathPlanner p = new PathPlanner(player, sg);

        assertEquals(SearchStatus.no_task, p.peekRequestStatus());
        assertTrue(p.getPath().isEmpty());


        p.createRequest(ItemType.HEALTH);

        // test status
        assertEquals(SearchStatus.search_incomplete, p.peekRequestStatus());
        assertTrue(p.getPath().isEmpty());

        while(p.cycleOnce()==SearchStatus.search_incomplete);
        assertEquals(SearchStatus.target_found, p.peekRequestStatus());

        List<PathEdge> path = p.getPath();
        assertTrue(!path.isEmpty());
        int size = path.size();
        path.clear();

        assertTrue(p.getPath().size()==size);


        assertTrue(p.createRequest(new Point2D(16*32, 16*32)));
        assertEquals(SearchStatus.search_incomplete, p.peekRequestStatus());
        assertTrue(p.getPath().isEmpty());
        while(p.cycleOnce()==SearchStatus.search_incomplete);
        assertEquals(SearchStatus.target_found, p.peekRequestStatus());

        List<PathEdge> path1 = p.getPath();
        assertTrue(!path1.isEmpty());
        int size1 = path1.size();
        path1.clear();

        assertTrue(p.getPath().size()==size1);
    }

    @Test
    @Parameters(method = "testParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void invalidTarget(GameCharacter player, SparseGraph sg) throws Exception {
        PathPlanner p = new PathPlanner(player, sg);
        assertFalse(p.createRequest(new Point2D(850,758)));
        assertEquals(SearchStatus.no_task, p.peekRequestStatus());
        assertTrue(p.getPath().isEmpty());
        assertEquals(SearchStatus.no_task, p.peekRequestStatus());
    }



    @Test
    @Parameters(method = "testParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void itemRequest(GameCharacter player, SparseGraph sg) throws Exception {
        PathPlanner p = new PathPlanner(player, sg);
        assertTrue(p.createRequest(ItemType.HEALTH));
        assertEquals(SearchStatus.search_incomplete, p.peekRequestStatus());
        assertTrue(p.getPath().isEmpty());
    }
}

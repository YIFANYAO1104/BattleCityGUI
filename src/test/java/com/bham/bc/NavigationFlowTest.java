package com.bham.bc;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.impl.PathEdge;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

/**
 * test the service flow for path planner
 * start -> incomplete -> (targetfound or targetnotfound)
 */
public class NavigationFlowTest {

    @Test
    public void test1() throws Exception {
        new JFXPanel();
        Player player = new Player(16*32, 16*32);
        GameMap gameMap = new GameMap(MapType.Map1);

        //gameMap.initGraph(player);
        //player.initNavigationService(gameMap.getGraph());

        PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);

        assertEquals(SearchStatus.no_task, field.get(p));
        assertTrue(p.getPath().isEmpty());


        p.createRequest(ItemType.HEALTH);

        // test status
        assertEquals(SearchStatus.search_incomplete, field.get(p));
        assertTrue(p.getPath().isEmpty());

        p.peekRequestStatus();
        assertEquals(SearchStatus.target_found, field.get(p));

        List<PathEdge> path = p.getPath();
        assertTrue(!path.isEmpty());
        int size = path.size();
        path.clear();

        assertTrue(p.getPath().size()==size);


        assertFalse(p.createRequest(new Point2D(-1,-1)));
        assertEquals(SearchStatus.no_task, field.get(p));
        assertTrue(p.getPath().isEmpty());
        assertEquals(SearchStatus.no_task, p.peekRequestStatus());
        assertEquals(SearchStatus.no_task, p.peekRequestStatus());
        assertEquals(SearchStatus.no_task, p.peekRequestStatus());


        assertTrue(p.createRequest(new Point2D(16*32, 16*32)));
        assertEquals(SearchStatus.search_incomplete, field.get(p));
        assertTrue(p.getPath().isEmpty());
        p.peekRequestStatus();
        assertEquals(SearchStatus.target_found, field.get(p));

        List<PathEdge> path1 = p.getPath();
        assertTrue(!path1.isEmpty());
        int size1 = path1.size();
        path1.clear();

        assertTrue(p.getPath().size()==size1);
    }



    @Test
    public void test2() throws Exception {
        new JFXPanel();
        Player player = new Player(-1, -1);
        GameMap gameMap = new GameMap(MapType.Map1);
        //gameMap.initGraph(player);
        //player.initNavigationService(gameMap.getGraph());
        PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);

        assertFalse(p.createRequest(ItemType.HEALTH));
        assertEquals(SearchStatus.no_task, field.get(p));
        assertTrue(p.getPath().isEmpty());
    }

    @Test
    public void test3() throws Exception {
        new JFXPanel();
        Player player = new Player(16*32, 16*32);
        GameMap gameMap = new GameMap(MapType.Map1);
        //gameMap.initGraph(player);
        //player.initNavigationService(gameMap.getGraph());

        PathPlanner p = new PathPlanner(player, gameMap.getGraph());

        p.createRequest(ItemType.HEALTH);
        p.peekRequestStatus();

        p.createRequest(ItemType.HEALTH);


        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);
        // test status
        assertEquals(SearchStatus.search_incomplete, field.get(p));
    }

    @Test
    public void testQuickSmooth() {
        //we need at least 2 path edges
        List<PathEdge> path = new ArrayList<>();
        path.add(new PathEdge(new Point2D(0,0),new Point2D(0,1)));
        path.add(new PathEdge(new Point2D(1,1),new Point2D(0,1)));
        path.add(new PathEdge(new Point2D(3,3),new Point2D(5,5)));
        path.add(new PathEdge(new Point2D(8,8),new Point2D(0,2)));

        System.out.println("Before: "+path);
        ListIterator<PathEdge> iterator = path.listIterator();

        //0th element in the list
        PathEdge e1 = iterator.next();

        while (iterator.hasNext()) {
            //increment e2 so it points to the edge following e1 (and futher)
            PathEdge e2 = iterator.next();
            //check for obstruction, adjust and remove the edges accordingly
            e1.setDestination(e2.getDestination());
            iterator.remove(); //remove e2 from the list

        }
        System.out.println("After: "+path);
    }
}

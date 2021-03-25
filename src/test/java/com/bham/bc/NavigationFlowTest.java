package com.bham.bc;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.SearchStatus;
import com.bham.bc.components.environment.navigation.impl.PathPlanner;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

/**
 * test the service flow for path planner
 * start -> incomplete -> (targetfound or targetnotfound)
 */
public class NavigationFlowTest {

    @Test
    public void test1() throws Exception {
        new JFXPanel();
        GameMap gameMap = new GameMap(MapType.Map1);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        Player player = new Player(16*32, 16*32);

        PathPlanner p = new PathPlanner(player, gameMap.getGraph());

        p.createRequest(ItemType.health);
        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);
        // test status
        assertEquals(SearchStatus.search_incomplete, field.get(p));
    }



    @Test
    public void test2() throws Exception {
        new JFXPanel();
        GameMap gameMap = new GameMap(MapType.Map1);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        Player player = new Player(16*32, 16*32);

        PathPlanner p = new PathPlanner(player, gameMap.getGraph());

        p.createRequest(ItemType.health);
        p.peekRequestStatus();
        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);
        // test status
        junit.framework.Assert.assertTrue(
                (field.get(p) == SearchStatus.target_found) ||
                        (field.get(p) ==  SearchStatus.target_not_found)
        );
    }

    @Test
    public void test3() throws Exception {
        new JFXPanel();
        GameMap gameMap = new GameMap(MapType.Map1);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        Player player = new Player(16*32, 16*32);

        PathPlanner p = new PathPlanner(player, gameMap.getGraph());

        p.createRequest(ItemType.health);
        p.peekRequestStatus();

        p.createRequest(ItemType.health);


        Field field = PathPlanner.class.getDeclaredField("taskStatus");
        // access private field
        field.setAccessible(true);
        // test status
        assertEquals(SearchStatus.search_incomplete, field.get(p));
    }
}

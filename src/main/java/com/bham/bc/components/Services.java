package com.bham.bc.components;

import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.enemies.EnemyType;
import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * <h1>Interface defining the required backend services</h1>
 *
 * <p>This is required for a mode to work properly. It mainly involves <i>adders</i> and <i>getters</i>.
 * This is because the controller will remove any entity automatically that has a property <b>exists</b>
 * as false and setters are managed by the entities themselves for safety reasons.</p>
 */
public interface Services {

    // ADDERS -----------------------------------------------------
    /**
     * Adds {@link com.bham.bc.components.shooting.Bullet} to the game
     * @param bullet Bullet object to be added to the list of bullets
     */
    void addBullet(Bullet bullet);

    /**
     * Adds {@link com.bham.bc.components.characters.GameCharacter} to the game
     * @param character GameCharacter object to be added to the list of characters
     */
    void addCharacter(GameCharacter character);

    /**
     * Adds {@link Trigger} to the game
     * @param trigger Trigger objects to be added to the list of triggers
     */
    void addTrigger(Trigger trigger);
    //-------------------------------------------------------------



    // GETTERS ----------------------------------------------------
    /**
     * Gets {@link com.bham.bc.entity.graph.SparseGraph} of a currently active map
     * @return SparseGraph object used for searching algorithms
     */
    SparseGraph<NavNode, GraphEdge> getGraph();

    /**
     * Gets all enemy areas initialized by the {@link com.bham.bc.components.environment.GameMap}
     * @return array of Circle objects representing enemy spawn areas
     */
    Circle[] getEnemyAreas();

    /**
     * Gets all home area initialized by the {@link com.bham.bc.components.environment.GameMap}
     * @return Circle object representing the home territory enemies can attack
     */
    Circle getHomeArea();
    //-------------------------------------------------------------



    // CALCULATIONS -----------------------------------------------
    /**
     * Gets the closest center coordinate determined by the straight distance to it from a provided point
     *
     * @param position point from where the closest cen position will be looked for
     * @param item     type of item to look for (e.g., <i>ALLY</i>, <i>ENEMY_AREA</i>)
     * @return Point2D object which points to the closest center position of the requested item or the provided
     *         position if there is no closest point
     */
    Point2D getClosestCenter(Point2D position, ItemType item);

    /**
     * Gets a random position of a free area requested around some center point
     *
     * <p>This method checks if there is a free area of the the requested radius withing a constrained area.
     * Constrained area is determined by a center position and a constraint radius. If <b>pos</b> is set to
     * <i>TOP_LEFT</i>, it will return the top left position of a circle (i.e., x - areaSize, y - areaSize),
     * in all other cases, it returns center position.</p>
     *
     * <p><b>Note:</b> if a moving entity happens to be spawned on top of another moving entity, they both will
     * be pushed away from each other's center position.</p>
     *
     * @param center         point around which the area should be found
     * @param constraint     radius for the center point within which the free area should be looked for
     * @param areaRadius     radius the free area should have
     * @param pos            TOP_LEFT or CENTER position requested to be returned
     * @param checkObstacles true or false saying if the obstacles should be checked for intersection
     * @return Point2D coordinate of a free area or (-1, -1) point if no area is found
     */
    //Point2D getFreeArea(Point2D center, double constraint, double areaRadius, Pos pos, boolean checkObstacles);

    /**
     * Checks if a path from <i>start</i> to <i>end</i> intersects any obstacles
     *
     * @param start  position where the path starts
     * @param end    position where the path ends
     * @param radius radius of an entity which should fit
     * @param array  list to which a straight rectangular path is added regardless if it intersects any obstacles
     * @return true if the calculated straight path intersects any obstacles and false otherwise
     */
    boolean canPass(Point2D start, Point2D end, Point2D radius, List<Shape> array);

    /**
     * Checks if a given rectangular shape intersects any non-walkable obstacles
     * @param area area of intersection to be checked for
     * @return true if there are interactive obstacles within the requested area and false otherwise
     */
    boolean intersectsObstacles(Rectangle area);
    // ------------------------------------------------------------



    // FRAME ITERATIONS -------------------------------------------
    /**
     * Updates all the entities: map, characters and armory elements
     */
    void update();

    /**
     * Renders backend content
     * @param gc graphics context where things will be rendered
     */
    void render(GraphicsContext gc);



    /**
     * Clears all objects in the game
     */
    void clear();
    //-------------------------------------------------------------



    // LOGIC ------------------------------------------------------

    /**
     * Changes the score of the game
     * @param score value to be added to the overall score
     */
    void changeScore(double score);

    /**
     * Simulates taking over home territory by decreasing home's "HP"
     * @param enemy enemy to be checked if it intersects with the home territory to take it over
     */
    void occupyHome(Enemy enemy);

    /**
     * Spawns enemy at a random enemy territory
     * @param enemyType type of enemy to spawn
     */
    void spawnEnemyRandomly(EnemyType enemyType);
    //-------------------------------------------------------------



    // UI ---------------------------------------------------------
    /**
     * Monitors the keyboard button presses and creates a corresponding GUI response
     * @param e key to handle
     */
    void keyPressed(KeyEvent e);

    /**
     * Monitors the keyboard button releases and creates a corresponding GUI response
     * @param e key to handle
     */
    void keyReleased(KeyEvent e);

    /**
     * Gets the score for the current game session based on how many enemies have been destroyed
     * @return score value to be shown on the screen
     */
    double getScore();

    /**
     * Gets the remaining home "HP" indicating how much territory is not yet taken over by the enemies
     * @return home "HP" value
     */
    double getHomeHp();

    /**
     * Gets the remaining home "HP" fraction indicating how much territory is not yet taken over by the enemies
     * @return home "HP" fraction value
     */
    double getHomeHpFraction();
    //-------------------------------------------------------------
}

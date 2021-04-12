package com.bham.bc.components;

import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Interface defining the required backend services</h1>
 *
 * <p>This is required for a mode to work properly. It mainly involves <i>adders</i> and <i>getters</i>.
 * This is because the controller will remove any entity automatically that has a property <b>exists</b>
 * as false and setters are managed by the entities themselves for safety reasons.</p>
 *
 * <br>TODO: remove temporary methods
 */
public interface BackendServices {

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
     * Gets the player's center position.
     * @return Point2D object containing x and y coordinates of a player
     */
    Point2D getPlayerCenterPosition();

    /**
     * Gets the positions of all the characters in the game
     * @return Point2D list with all character locations
     */
    ArrayList<Point2D> allCharacterPositions();

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
    //-------------------------------------------------------------



    // OTHER ------------------------------------------------------
    /**
     * Updates all the entities: map, characters and armory elements
     */
    void update();

    /**
     * Clears all objects in the game
     */
    void clear();

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
    //-------------------------------------------------------------



    // TEMPORARY METHODS -------------------------------------------
    // TODO: replace / remove or find another usage
    boolean intersectsObstacles(Rectangle hitbox);  // This will be moved to physics package
    Circle[] getEnemyAreas();
    Circle getHomeArea();
    void occupyHome(Enemy enemy);
    Point2D getClosestCenter(Point2D position, ItemType item);
}

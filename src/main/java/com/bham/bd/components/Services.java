package com.bham.bd.components;

import com.bham.bd.components.characters.Player;
import com.bham.bd.components.characters.Side;
import com.bham.bd.components.characters.agents.Agent;
import com.bham.bd.components.characters.agents.enemies.EnemyType;
import com.bham.bd.components.shooting.Bullet;
import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.components.triggers.Trigger;
import com.bham.bd.components.triggers.TriggerType;
import com.bham.bd.entity.BaseGameEntity;
import com.bham.bd.entity.ai.navigation.ItemType;
import com.bham.bd.entity.ai.navigation.AlgorithmDriver;
import com.bham.bd.entity.graph.SparseGraph;
import com.bham.bd.entity.graph.edge.GraphEdge;
import com.bham.bd.entity.graph.node.NavNode;
import com.bham.bd.entity.physics.MapDivision;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

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
     * Adds {@link com.bham.bd.components.shooting.Bullet} to the game
     * @param bullet Bullet object to be added to the list of bullets
     */
    void addBullet(Bullet bullet);

    /**
     * Adds {@link com.bham.bd.components.characters.GameCharacter} to the game
     * @param character GameCharacter object to be added to the list of characters
     */
    void addCharacter(GameCharacter character);


    /**
     * Adds {@link Trigger} to the game
     * @param trigger
     */
    void addTrigger(Trigger trigger);
    //-------------------------------------------------------------



    // GETTERS ----------------------------------------------------
    MapDivision<BaseGameEntity> getMapDivision();
    ArrayList<GameCharacter> getCharacters();
    /**
     * Gets the graph of a currently active map
     * @return {@link SparseGraph} object used for searching algorithms
     */
    SparseGraph<NavNode, GraphEdge> getGraph();

    /**
     * Gets the search algorithm runner of a currently active game session
     * @return {@link AlgorithmDriver} object used for pathfinding
     */
    AlgorithmDriver getDriver();

    /**
     * Gets all the characters of the requested side contained in the active game session
     * @param side {@code ALLY} or {@code ENEMY} side the characters belong to
     * @return a list of characters belonging to the requested side
     */
    ArrayList<GameCharacter> getCharacters(Side side);

    /**
     * Gets the player of the current game session
     * @return {@code Player} object that the user controls or {@code null} if it doesn't exist
     */
    Player getPlayer();

    /**
     * Gets all enemy areas initialized by the {@link com.bham.bd.components.environment.GameMap}
     * @return array of Circle objects representing enemy spawn areas
     */
    Circle[] getEnemyAreas();

    /**
     * Gets all home area initialized by the {@link com.bham.bd.components.environment.GameMap}
     * @return Circle object representing the home territory enemies can attack
     */
    Circle getHomeArea();

    /**
     * Gets all the active bullets in the game for testing purposes
     * @return a list of active bullets in the game
     */
    ArrayList<Bullet> getBullets();
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
     * Gets the closest ALLY side character coordinate determined by the straight distance to it from a provided point
     * @param position point from where the closest cen position will be looked for
     * @return closest GameCharacter from Side ALLY
     */
    GameCharacter getClosestALLY(Point2D position);

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
     * @param center      point around which the area should be found
     * @param innerRadius radius for the center point within which the free area cannot appear
     * @param outerRadius radius for the center point within which the free area should be looked for
     * @param areaSize    size the free area should have (length of one side of a square)
     * @param pos         TOP_LEFT or CENTER position requested to be returned
     * @return Point2D coordinate of a free area or {@code null} if no area is found
     */
    Point2D getFreeArea(Point2D center, double innerRadius, double outerRadius, double areaSize, Pos pos);

    /**
     * Checks if a given rectangular shape intersects any non-walkable obstacles
     * @param area area of intersection to be checked for
     * @return true if there are interactive obstacles within the requested area and false otherwise
     */
    boolean intersectsObstacles(Rectangle area);
    // ------------------------------------------------------------



    // FRAME ITERATIONS -------------------------------------------
    /**
     * Updates all the entities: map, characters and shooting elements
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
     * @param agent enemy to be checked if it intersects with the home territory to take it over
     */
    void occupyHome(Agent agent);

    /**
     * Spawns enemy at a random enemy territory
     * @param enemyType type of enemy to spawn
     */
    void spawnEnemyRandomly(EnemyType enemyType);

    /**
     * Spawns an ally at the home base
     */
    void spawnAlly();

    /**
     * Spawns trigger randomly around a given point
     * @param triggerType     type of trigger to spawn (most power-ups and traps can be spawned)
     * @param center          center position around which the trigger will be spawned
     * @param innerConstraint inner radius within the trigger won't spawn
     * @param outerConstraint outer radius within the trigger will spawn
     * @return {@code true} if the trigger was spawned successfully and {@code false} otherwise
     */
    boolean spawnTriggerAroundPoint(TriggerType triggerType, Point2D center, double innerConstraint, double outerConstraint);
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
     * Gets the remaining home "HP" fraction indicating how much territory is not yet taken over by the enemies
     * @return home "HP" fraction value
     */
    double getHomeHpFraction();

    /**
     * Gets the remaining player "HP" fraction showing how much health the player has left
     * @return player "HP" fraction value
     */
    double getPlayerHpFraction();

    /**
     * Checks if the player has reached a game over state
     * @return {@code true} if the user has lost and {@code false} otherwise
     */
    boolean gameOver();
    //-------------------------------------------------------------
}

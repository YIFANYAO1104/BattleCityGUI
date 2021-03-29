package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import com.bham.bc.entity.physics.BombTank;

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
     * Adds {@link com.bham.bc.components.armory.Bullet} to the game
     * @param bullet Bullet object to be added to the list of bullets
     */
    void addBullet(Bullet bullet);

    /**
     * Adds {@link com.bham.bc.components.characters.GameCharacter} to the game
     * @param character GameCharacter object to be added to the list of characters
     */
    void addCharacter(GameCharacter character);

    /**
     * Adds {@link com.bham.bc.entity.triggers.Trigger} to the game
     * @param trigger Trigger objects to be added to the list of triggers
     */
    void addTrigger(Trigger trigger);
    //-------------------------------------------------------------



    // GETTERS ----------------------------------------------------
    /**
     * Gets {@link com.bham.bc.utils.graph.SparseGraph} of a currently active map
     * @return SparseGraph object used for searching algorithms
     */
    SparseGraph getGraph();

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
    //-------------------------------------------------------------



    // TEMPORARY METHODS -------------------------------------------
    // TODO: replace / remove or find another usage
    void addBombTank(BombTank b);               // We don't have effects yet or it can be a trigger
    boolean intersectsObstacles(Shape hitbox);  // This will be moved to physics package
    Point2D getMapCenterPosition(); // TODO: doc
    Point2D getNearestOppositeSideCenterPosition(Point2D point, SIDE side); //TODO: doc
}

package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.DefaultEnemy;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.characters.enemies.Enemy;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface defining the required backend services for a mode to properly work
 */
public interface BackendServices {
    /**
     * Adds enemy to the game
     * @param enemy Enemy object to be added
     */
    void addEnemy(Enemy enemy);

    /**
     * Adds a bullet to the bullet list
     * @param bullet bullet to be added to the bullet list
     */
    void addBullet(Bullet bullet);

    /**
     * Gets all the characters in the game
     * @return list of all the characters
     */
    ArrayList<Character> getCharacters();

    /** TODO: move to physics, document or replace */
    boolean intersectsCharacters(Shape hitBox, List<Character> characters);
    boolean intersectsObstacles(Shape hitBox);
    int getPlayerID();
    Point2D getPlayerCenterPosition();

    /**
     * Updates all the entities: map, characters and armory elements
     */
    void update();

    /**
     * Clears all objects in the game
     */
    void clear();
}

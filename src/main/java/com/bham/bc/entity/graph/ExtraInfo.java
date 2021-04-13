package com.bham.bc.entity.graph;

import com.bham.bc.entity.ai.navigation.ItemType;

/**
 * Bring implemented if an entity in the game is expected to be found by searching algorithm
 * For example, Trigger class
 */
public interface ExtraInfo {
    /**
     * Check whether that entity is active or not
     * @return true if the entity is active
     */
    boolean active();

    /**
     * Check what type of entity it is
     * @return {@link ItemType} The type of item
     */
    ItemType getItemType();
}

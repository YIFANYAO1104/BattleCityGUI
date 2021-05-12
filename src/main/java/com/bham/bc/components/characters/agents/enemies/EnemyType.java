package com.bham.bc.components.characters.agents.enemies;

import com.bham.bc.components.characters.agents.Agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Enum describing the type of enemy
 */
public enum EnemyType {
    NOVA(),
    SHOOTER(),
    SPLITTER(),
    TANK(),
    TEASER(),
    TRAPPER();

    /**
     * Default constructor
     */
    EnemyType() { }

    /**
     * Creates a new instance of an enemy class of type corresponding to this enum
     *
     * <p><b>Note:</b> requested enemy class must be in the same package as this enum and must pass only 2 attributes, i.e.,
     * <i>x</i> and <i>y</i> coordinates.</p>
     *
     * @param x top left coordinate in x axis to spawn the requested enemy
     * @param y top left coordinate in y axis to spawn the requested enemy
     * @return a new instance of a specific child class of {@link Agent} object
     *
     * @throws ClassNotFoundException    thrown if class name could not be found in {@link com.bham.bc.components.characters.agents.enemies}
     * @throws NoSuchMethodException     thrown if constructor of a requested class has wrong type
     * @throws IllegalAccessException    thrown when there is no access to the constructor of a requested class
     * @throws InstantiationException    thrown if the requested class has abstract type
     * @throws InvocationTargetException thrown if the constructor of the requested class throws an exception
     */
    public Agent newInstance(double x, double y) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String packageName = EnemyType.class.getPackage().getName();
        String className = toString().substring(0,1).toUpperCase() + toString().substring(1).toLowerCase();

        try {
            Class<?> cls = Class.forName(packageName + "." + className);
            Constructor<?> cons = cls.getConstructor(double.class, double.class);
            return (Agent) cons.newInstance(x, y);
        } catch (ClassNotFoundException e) {
            System.out.println("Make sure " + className + " class is in " + packageName + " package");
            throw e;
        } catch (NoSuchMethodException e) {
            System.out.println("Make sure the constructor of " + className + " accepts only x and y double values");
            throw e;
        }
    }
}

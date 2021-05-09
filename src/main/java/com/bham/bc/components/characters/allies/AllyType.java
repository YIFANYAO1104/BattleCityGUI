package com.bham.bc.components.characters.allies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Enum describing the type of ally
 */
public enum AllyType {
    NEURON();

    AllyType() { }

    /**
     * Creates a new instance of an ally class of type corresponding to this enum
     *
     * <p><b>Note:</b> requested ally class must be in the same package as this enum and must pass only 2 attributes, i.e.,
     * <i>x</i> and <i>y</i> coordinates.</p>
     *
     * @param x top left coordinate in x axis to spawn the requested ally
     * @param y top left coordinate in y axis to spawn the requested ally
     * @return a new instance of a specific child class of {@link Ally} object
     *
     * @throws ClassNotFoundException    thrown if class name could not be found in {@link com.bham.bc.components.characters.enemies}
     * @throws NoSuchMethodException     thrown if constructor of a requested class has wrong type
     * @throws IllegalAccessException    thrown when there is no access to the constructor of a requested class
     * @throws InstantiationException    thrown if the requested class has abstract type
     * @throws InvocationTargetException thrown if the constructor of the requested class throws an exception
     */
    public Ally newInstance(double x, double y) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String packageName = AllyType.class.getPackage().getName();
        String className = toString().substring(0,1).toUpperCase() + toString().substring(1).toLowerCase();

        try {
            Class<?> cls = Class.forName(packageName + "." + className);
            Constructor<?> cons = cls.getConstructor(double.class, double.class);
            return (Ally) cons.newInstance(x, y);
        } catch (ClassNotFoundException e) {
            System.out.println("Make sure " + className + " class is in " + packageName + " package");
            throw e;
        } catch (NoSuchMethodException e) {
            System.out.println("Make sure the constructor of " + className + " accepts only x and y double values");
            throw e;
        }
    }
}

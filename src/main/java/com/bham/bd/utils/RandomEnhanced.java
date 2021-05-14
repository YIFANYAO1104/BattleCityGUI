package com.bham.bd.utils;

import java.util.Random;

/**
 * a util class based on Random
 */
public class RandomEnhanced {
    public static Random rand = new Random();

    /**
     * generate a random integer in [lowerBound,upperBound]
     * @param lowerBound the left most integer
     * @param upperBound the right most integer
     * @return an integer in [lowerBound,upperBound]
     */
    static public int randInt(int lowerBound, int upperBound){
        return rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    /**
     * generate a random double in [rangeMin,rangeMax]
     * @param rangeMin the left most integer
     * @param rangeMax the right most integer
     * @return an double in [rangeMin,rangeMax]
     */
    public static double randDouble(double rangeMin, double rangeMax){
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }
}

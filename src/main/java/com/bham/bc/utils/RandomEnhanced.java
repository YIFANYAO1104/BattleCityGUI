package com.bham.bc.utils;

import java.util.Random;

public class RandomEnhanced {
    public static Random rand = new Random();

    static public int randInt(int lowerBound, int upperBound){
        return rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    public static double randDouble(double rangeMin, double rangeMax){
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }
}

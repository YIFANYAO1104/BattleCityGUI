package com.bham.bc.utils;

import java.util.Random;

public class RandomEnhanced {
    private static Random rand = new Random();

    public static int randInt(int lowerBound, int upperBound){
        return rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }
}

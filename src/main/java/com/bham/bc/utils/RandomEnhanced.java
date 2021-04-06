package com.bham.bc.utils;

import javafx.geometry.Point2D;

import java.util.Random;

public class RandomEnhanced {

    static private Random rand = new Random();


    public static double randDouble(double rangeMin, double rangeMax){
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }

    //https://stackoverflow.com/questions/5837572/generate-a-random-point-within-a-circle-uniformly
    public static Point2D randomPointInCircle(Point2D centerPos, double radius) {
        Double theta = 2*Math.PI*rand.nextDouble();
        Double r = Math.sqrt(rand.nextDouble())*radius;
        Point2D target = new Point2D(Math.cos(theta),Math.sin(theta))
                        .multiply(r)
                        .add(centerPos);
        return target;
    }

}

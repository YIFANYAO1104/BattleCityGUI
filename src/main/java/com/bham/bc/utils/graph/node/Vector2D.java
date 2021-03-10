package com.bham.bc.utils.graph.node;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D() {
        x = 0.0;
        y = 0.0;
    }

    public Vector2D(double a, double b) {
        x = a;
        y = b;
    }

    public Vector2D(Vector2D v) {
        super();
        this.set(v);
    }

    public Vector2D set(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    //sets x and y to zero

    public void Zero() {
        x = 0.0;
        y = 0.0;
    }

    //returns true if both x and y are zero
    public boolean isZero() {
        return (x * x + y * y) < Double.MIN_VALUE;
    }

    /**
     *   returns the length of a 2D vector
     */
    public double Length() {
        return Math.sqrt(x * x + y * y);
    }

    //returns the squared length of the vector (thereby avoiding the sqrt)
    public double LengthSq() {
        return (x * x + y * y);
    }

    public double Distance(Vector2D v2) {
        double ySeparation = v2.y - y;
        double xSeparation = v2.x - x;

        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }

    public static double Vec2DDistance(Vector2D v1, Vector2D v2) {

        double ySeparation = v2.y - v1.y;
        double xSeparation = v2.x - v1.x;

        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }
    public  Vector2D mines(Vector2D v2){
        return new Vector2D(x- v2.getX(),y- v2.getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString(){
        String location = "x size is :"+x + " , y size is :"+y;
        return location;
    }

}

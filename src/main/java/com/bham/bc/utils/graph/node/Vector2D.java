package com.bham.bc.utils.graph.node;

public class Vector2D {
    public double x;
    public double y;

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

}

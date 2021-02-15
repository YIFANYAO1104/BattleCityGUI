/**
 *   2D vector struct
 *   @author Petr Bilek (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;


public class Vector2D {

    public int x;
    public int y;

    public Vector2D() {
        x = 0;
        y = 0;
    }

    public Vector2D(int a, int b) {
        x = a;
        y = b;
    }

    public static double Vec2DDistanceSq(Vector2D v1, Vector2D v2) {

        double ySeparation = v2.y - v1.y;
        double xSeparation = v2.x - v1.x;

        return ySeparation * ySeparation + xSeparation * xSeparation;
    }
}
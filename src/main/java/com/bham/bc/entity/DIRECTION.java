package com.bham.bc.entity;

import javafx.geometry.Point2D;

/**
 * Represents 4 valid directions: up, down, left, right
 */
public enum DIRECTION {
	U(0, 1),
	D(0, -1),
	L(-1, 0),
	R(1, 0);

	private Point2D point2D;

	/**
	 * Constructs DIRECTION as a basis vector in 2D space
	 *
	 * @param x basis coordinate in x axis
	 * @param y basis coordinate in y axis
	 */
	DIRECTION(int x, int y) { point2D = new Point2D(x, y); }

	/**
	 * Gets the value of enum as a point (basis vector)
	 * @return Point2D object with one coordinate in specific direction defined by this enum value
	 */
	public Point2D toPoint() { return point2D; }
}

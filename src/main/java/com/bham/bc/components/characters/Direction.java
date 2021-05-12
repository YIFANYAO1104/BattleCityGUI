package com.bham.bc.components.characters;

import javafx.geometry.Point2D;

/**
 * Represents 4 valid directions: up, down, left, right
 */
public enum Direction {
	U(0, -1),
	D(0, 1),
	L(-1, 0),
	R(1, 0);

	/** Orthonormal basis vector of this direction (can be negative) */
	private final Point2D POINT;

	/**
	 * Constructs DIRECTION as a basis vector in 2D space
	 *
	 * @param x basis coordinate in x axis
	 * @param y basis coordinate in y axis
	 */
	Direction(int x, int y) {
		POINT = new Point2D(x, y);
	}

	/**
	 * Gets the value of enum as a point (basis vector)
	 * @return {@code Point2D} object with one coordinate in specific direction defined by this enum value
	 */
	public Point2D toPoint() {
		return POINT;
	}
}

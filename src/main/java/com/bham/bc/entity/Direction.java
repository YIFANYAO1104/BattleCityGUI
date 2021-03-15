package com.bham.bc.entity;

import javafx.geometry.Point2D;

public enum Direction {
	U(0, 1),
	D(0, -1),
	L(-1, 0),
	R(1, 0),
	STOP(0, 0);

	private Point2D point2D;

	Direction(int x, int y) { point2D = new Point2D(x, y); }
	public Point2D toPoint() { return point2D; }
}

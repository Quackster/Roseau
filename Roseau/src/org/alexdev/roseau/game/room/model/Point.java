package org.alexdev.roseau.game.room.model;

public class Point {

	private int X;
	private int Y;
	private double Z;

	public Point() {
		this(0, 0, 0);
	}

	public Point(int x, int y) {
		this.X = x;
		this.Y = y;
		this.Z = 0;
	}

	public Point(int x, int y, double i) {
		this.X = x;
		this.Y = y;
		this.Z = i;
	}

	public int getX() {
		return X;
	}

	public void setX(int x) {
		X = x;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}

	public double getZ() {
		return Z;
	}

	public void setZ(double z) {
		Z = z;
	}

	public Point add(Point other) {
		return new Point(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
	}

	public Point subtract(Point other) {
		return new Point(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
	}


	public int getDistanceSquared(Point point) {
		int dx = this.getX() - point.getX();
		int dy = this.getY() - point.getY();

		return (dx * dx) + (dy * dy);
	}

	public boolean sameAs(Point point) {	
		return (this.X == point.getX() && this.Y == point.getY());
	}

	@Override
	public String toString() {

		return "[" + this.X + ", " + this.Y + "]";
	}

}

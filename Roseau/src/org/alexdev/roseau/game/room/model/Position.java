package org.alexdev.roseau.game.room.model;

public class Position {

	private int X;
	private int Y;
	private double Z;

	public Position() {
		this(0, 0, 0);
	}

	public Position(int x, int y) {
		this.X = x;
		this.Y = y;
		this.Z = 0;
	}

	public Position(int x, int y, double i) {
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

	public Position add(Position other) {
		return new Position(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
	}

	public Position subtract(Position other) {
		return new Position(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
	}


	public int getDistanceSquared(Position point) {
		int dx = this.getX() - point.getX();
		int dy = this.getY() - point.getY();

		return (dx * dx) + (dy * dy);
	}
	
	public int getDistance(Position point) {
return (int)Math.hypot(this.getX() - point.getX(), this.getY() - point.getY());
	}
	
	
	

	public boolean sameAs(Position point) {	
		return (this.X == point.getX() && this.Y == point.getY());
	}

	@Override
	public String toString() {

		return "[" + this.X + ", " + this.Y + "]";
	}

}

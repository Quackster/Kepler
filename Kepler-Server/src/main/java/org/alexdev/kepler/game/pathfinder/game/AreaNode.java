package org.alexdev.kepler.game.pathfinder.game;

import org.alexdev.kepler.game.pathfinder.Position;

public class AreaNode {

	public final Position point;

	public AreaNode parent;

	public int gValue; //points from start
	public int hValue; //distance from target
	public boolean isWall = false;

	private final int MOVEMENT_COST = 10;

	public AreaNode(Position point) {
		this.point = point;
	}

	/**
	 * Used for setting the starting node value to 0
	 */
	public void setGValue(int amount) {
		this.gValue = amount;
	}

	public void calculateHValue(AreaNode destPoint) {
		this.hValue = (Math.abs(point.getX() - destPoint.point.getX()) + Math.abs(point.getY() - destPoint.point.getY())) * this.MOVEMENT_COST;
	}

	public void calculateGValue(AreaNode point) {
		this.gValue = point.gValue + this.MOVEMENT_COST;
	}

	public int getFValue() {
		return this.gValue + this.hValue;
	}
}
package org.alexdev.roseau.game.pathfinder;

import org.alexdev.roseau.game.room.model.Point;

public class PathfinderNode implements Comparable<PathfinderNode> {
	
    private Point position;
    private PathfinderNode nextNode;

    private Integer cost = Integer.MAX_VALUE;
    private boolean inOpen = false;
    private boolean inClosed = false;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PathfinderNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(PathfinderNode nextNode) {
        this.nextNode = nextNode;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isInOpen() {
        return inOpen;
    }

    public void setInOpen(boolean inOpen) {
        this.inOpen = inOpen;
    }

    public boolean isInClosed() {
        return inClosed;
    }

    public void setInClosed(boolean inClosed) {
        this.inClosed = inClosed;
    }

    public PathfinderNode(Point current) {
        this.position = current;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PathfinderNode) && ((PathfinderNode) obj).getPosition().equals(this.position);
    }

    public boolean equals(PathfinderNode node) {
        return node.getPosition().equals(this.position);
    }

    @Override
    public int hashCode() {
        return this.position.hashCode();
    }

    @Override
    public int compareTo(PathfinderNode o) {
        return this.getCost().compareTo(o.getCost());
    }
}

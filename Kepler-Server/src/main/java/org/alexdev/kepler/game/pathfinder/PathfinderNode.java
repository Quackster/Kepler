package org.alexdev.kepler.game.pathfinder;

public class PathfinderNode implements Comparable<PathfinderNode> {

    private Position position;
    private PathfinderNode nextNode;
    private Integer cost = Integer.MAX_VALUE;
    private boolean inOpen = false;
    private boolean inClosed = false;

    /**
     * Gets the position.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets the next node.
     *
     * @return the next node
     */
    public PathfinderNode getNextNode() {
        return nextNode;
    }

    /**
     * Sets the next node.
     *
     * @param nextNode the new next node
     */
    public void setNextNode(PathfinderNode nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Gets the cost.
     *
     * @return the cost
     */
    public Integer getCost() {
        return cost;
    }

    /**
     * Sets the cost.
     *
     * @param cost the new cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Checks if is in open.
     *
     * @return true, if is in open
     */
    public boolean isInOpen() {
        return inOpen;
    }

    /**
     * Sets the in open.
     *
     * @param inOpen the new in open
     */
    public void setInOpen(boolean inOpen) {
        this.inOpen = inOpen;
    }

    /**
     * Checks if is in closed.
     *
     * @return true, if is in closed
     */
    public boolean isInClosed() {
        return inClosed;
    }

    /**
     * Sets the in closed.
     *
     * @param inClosed the new in closed
     */
    public void setInClosed(boolean inClosed) {
        this.inClosed = inClosed;
    }

    /**
     * Instantiates a new pathfinder node.
     *
     * @param current the current
     */
    public PathfinderNode(Position current) {
        this.position = current;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PathfinderNode) && ((PathfinderNode) obj).getPosition().equals(this.position);
    }

    /**
     * Equals.
     *
     * @param node the node
     * @return true, if successful
     */
    public boolean equals(PathfinderNode node) {
        return node.getPosition().equals(this.position);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.position.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PathfinderNode o) {
        return this.getCost().compareTo(o.getCost());
    }
}

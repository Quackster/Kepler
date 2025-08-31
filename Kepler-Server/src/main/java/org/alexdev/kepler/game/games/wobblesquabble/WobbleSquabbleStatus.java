package org.alexdev.kepler.game.games.wobblesquabble;

public class WobbleSquabbleStatus {
    private int position;
    private int balance;
    private WobbleSquabbleMove move;
    private boolean hit;

    public WobbleSquabbleStatus(int position, int balance, WobbleSquabbleMove move, boolean isHit) {
        this.position = position;
        this.balance = balance;
        this.move = move;
        this.hit = isHit;
    }

    /**
     * Get the current position of the player.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the current position of the player.
     *
     * @param position the position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Get the current balance of the player.
     *
     * @return the current balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Set the balance of the player.
     *
     * @param balance the balance
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Get the current move the player.
     *
     * @return the move
     */
    public WobbleSquabbleMove getMove() {
        return move;
    }

    /**
     * Get whether the player has been hit.
     *
     * @return true, if successful
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Set whether the player has been hit.
     *
     * @param hit true, if successful
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }
}

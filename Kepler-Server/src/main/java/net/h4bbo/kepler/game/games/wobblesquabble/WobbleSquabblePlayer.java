package net.h4bbo.kepler.game.games.wobblesquabble;

import net.h4bbo.kepler.game.player.Player;

public class WobbleSquabblePlayer {
    private Player player;
    private WobbleSquabbleGame wsGame;
    private int position;
    private int balance;
    private boolean rebalanced;
    private boolean hit;
    private boolean requiresUpdate;
    private WobbleSquabbleMove move;
    private int order;

    public WobbleSquabblePlayer(WobbleSquabbleGame wsGame, Player player, int order) {
        this.player = player;
        this.wsGame = wsGame;
        this.move = WobbleSquabbleMove.NONE;
        this.order = order;
        this.balance = 0;
    }

    /**
     * Get if the player is balancing correctly.
     *
     * @return true, if successful
     */
    public boolean isBalancing() {
        return (this.balance > -100 && this.balance < 100);
    }

    public int getScore() {
        if (this.isBalancing()) {
            if (balance > 0 || balance == 0)
                return 100 - balance;
            else
                return 100 + balance;
        } else {
            return 0;
        }
    }

    /**
     * Reset the actions of the user.
     */
    public void resetActions() {
        this.move = WobbleSquabbleMove.NONE;
        this.requiresUpdate = false;
        this.hit = false;
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
     * Get whether they've used rebalance.
     *
     * @return true, if successful
     */
    public boolean isRebalanced() {
        return rebalanced;
    }

    /**
     * Set wehther if they've used rebalance.
     *
     * @param rebalanced true, if successful
     */
    public void setRebalanced(boolean rebalanced) {
        this.rebalanced = rebalanced;
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

    /**
     * Get whether the wobble squabble player requires an update.
     *
     * @return true, if successful
     */
    public boolean isRequiresUpdate() {
        return requiresUpdate;
    }

    /**
     * Set whether the wobble squabble player requires an update.
     *
     * @param requiresUpdate true, if successful
     */
    public void setRequiresUpdate(boolean requiresUpdate) {
        this.requiresUpdate = requiresUpdate;
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
     * Set the current move the player.
     *
     * @param move the move
     */
    public void setMove(WobbleSquabbleMove move) {
        this.move = move;
    }

    /**
     * Get the wobble squabble game instance.
     *
     * @return the game instance
     */
    public WobbleSquabbleGame getGame() {
        return wsGame;
    }

    /**
     * Gets the player instance of the wobble squabble player.
     *
     * @return the player instance
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the order of the player, 1 or 0.
     *
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order of the game player.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }
}

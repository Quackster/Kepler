package org.alexdev.kepler.messages.types;

import org.alexdev.kepler.game.player.Player;

public abstract class PlayerMessageComposer extends MessageComposer {
    private Player player;

    /**
     * The player currently handling the packet for
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player currently being handled for.
     *
     * @param player the player
     */
    public void setPlayer(Player player) throws Exception {
        if (this.player != null) {
            throw new Exception("The player has already been set!");
        }

        this.player = player;
    }
}

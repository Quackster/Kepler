package org.alexdev.kepler.game.room.components;

import org.alexdev.kepler.game.player.Player;

public interface RoomComponent {
    void onPlayerAdded(Player player);

    void onPlayerRemoved(Player player);
}

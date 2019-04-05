package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public interface ModerationAction {
    void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader);
}


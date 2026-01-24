package net.h4bbo.kepler.game.moderation;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public interface ModerationAction {
    void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader);
}


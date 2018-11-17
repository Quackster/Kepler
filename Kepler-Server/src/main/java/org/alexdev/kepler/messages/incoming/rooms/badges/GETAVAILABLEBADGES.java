package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETAVAILABLEBADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new AVAILABLE_BADGES(player.getDetails()));
    }
}
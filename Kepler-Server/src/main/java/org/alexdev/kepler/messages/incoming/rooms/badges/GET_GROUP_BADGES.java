package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.GROUP_BADGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_GROUP_BADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new GROUP_BADGES());
    }
}
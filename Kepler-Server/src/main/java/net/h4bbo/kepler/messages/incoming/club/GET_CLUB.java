package net.h4bbo.kepler.messages.incoming.club;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GET_CLUB implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.refreshClub();
    }
}

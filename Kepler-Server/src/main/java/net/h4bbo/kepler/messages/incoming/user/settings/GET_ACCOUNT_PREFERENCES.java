package net.h4bbo.kepler.messages.incoming.user.settings;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.user.settings.ACCOUNT_PREFERENCES;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GET_ACCOUNT_PREFERENCES implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new ACCOUNT_PREFERENCES(player.getDetails()));
    }
}
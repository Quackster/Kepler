package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.game.messenger.MessengerError;
import net.h4bbo.kepler.game.messenger.MessengerErrorType;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.messenger.FRIENDS_UPDATE;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_ERROR;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.config.GameConfiguration;

public class FRIENDLIST_UPDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new FRIENDS_UPDATE(player, player.getMessenger()));
    }
}

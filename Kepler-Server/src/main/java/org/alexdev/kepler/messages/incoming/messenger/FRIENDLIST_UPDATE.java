package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.messenger.FRIENDS_UPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class FRIENDLIST_UPDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new FRIENDS_UPDATE(player, player.getMessenger()));
    }
}

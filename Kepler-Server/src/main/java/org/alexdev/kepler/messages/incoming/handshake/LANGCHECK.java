package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.alert.EPS_NOTIFY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class LANGCHECK implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        String text = reader.readString();
        System.out.println(text);
        player.send(new EPS_NOTIFY(text));
    }
}

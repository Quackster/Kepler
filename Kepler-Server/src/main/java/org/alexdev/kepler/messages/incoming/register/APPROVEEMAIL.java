package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.register.EMAIL_APPROVED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class APPROVEEMAIL implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.isLoggedIn()) {
            return;
        }

        String email = reader.readString();

        player.send(new EMAIL_APPROVED());
    }
}

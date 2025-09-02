package net.h4bbo.kepler.messages.incoming.register;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.register.EMAIL_APPROVED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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

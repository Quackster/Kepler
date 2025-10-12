package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.register.TOTP_EMAIL_VERIFICATION_REQUIRED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TOTP_EMAIL_REQUIRED implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String email = reader.readString();

        player.send(new TOTP_EMAIL_VERIFICATION_REQUIRED(false));
    }
}

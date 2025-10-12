package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.handshake.SECRET_KEY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GENERATEKEY implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.isLoggedIn()) {
            return;
        }

        final String publicKey = reader.readString();

        player.getDiffieHellman().generateSharedKey(publicKey);

        player.send(new SECRET_KEY(player.getDiffieHellman().getPublicKey().toString()));
        player.setDecoder(player.getDiffieHellman().getSharedKey());

        //player.send(new SESSION_PARAMETERS(player.getDetails()));

        //if (player.getVersion() <= 17) {
        //player.send(new AVAILABLE_SETS("[" + GameConfiguration.getInstance().getString("users.figure.parts.default") + "]"));
        //}
    }
}

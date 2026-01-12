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

        player.getDiffieHellman().setRemotePublicKey(publicKey);

        player.send(new SECRET_KEY(player.getDiffieHellman().generatePublicKey()));
        player.setEncryptionKeys(player.getDiffieHellman());
    }
}

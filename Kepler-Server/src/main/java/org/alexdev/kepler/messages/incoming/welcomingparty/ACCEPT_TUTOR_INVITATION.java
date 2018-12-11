package org.alexdev.kepler.messages.incoming.welcomingparty;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ACCEPT_TUTOR_INVITATION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        // The client sends the user ID as string, god knows why
        String userId = reader.readString();

        System.out.println(userId);
    }
}
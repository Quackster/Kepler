package net.h4bbo.kepler.messages.incoming.welcomingparty;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class REJECT_TUTOR_INVITATION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        // The client sends the user ID as string, god knows why
        String userId = reader.readString();
        //System.out.println(userId);
    }
}
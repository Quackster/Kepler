package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.game.messenger.MessengerMessage;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_MSG;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_GETMESSAGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        for (MessengerMessage offlineMessage : player.getMessenger().getOfflineMessages().values()) {
            player.send(new MESSENGER_MSG(offlineMessage));
        }
    }
}

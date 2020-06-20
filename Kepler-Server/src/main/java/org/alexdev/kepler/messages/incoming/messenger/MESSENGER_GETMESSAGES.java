package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.messenger.CAMPAIGN_MSG;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_MSG;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_GETMESSAGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        for (MessengerMessage offlineMessage : player.getMessenger().getOfflineMessages().values()) {
            if(offlineMessage.getFromId() == 0) {
                player.send(new CAMPAIGN_MSG(offlineMessage));
            } else {
                player.send(new MESSENGER_MSG(offlineMessage));
            }
        }
    }
}

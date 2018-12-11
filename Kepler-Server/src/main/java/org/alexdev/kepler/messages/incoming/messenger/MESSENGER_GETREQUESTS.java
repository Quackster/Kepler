package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.messenger.FRIEND_REQUEST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_GETREQUESTS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        for (MessengerUser requester : player.getMessenger().getRequests()) {
            player.send(new FRIEND_REQUEST(requester));
        }
    }
}

package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_DECLINEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        boolean declineAll = reader.readBoolean();
        if (declineAll) {
            player.getMessenger().declineAllRequests();
            return;
        }

        int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            int userId = reader.readInt();
            declineBuddy(player, userId);
        }
    }

    private void declineBuddy(Player player, int userId) {
        if (!player.getMessenger().hasRequest(userId)) {
            return;
        }

        MessengerUser requester = player.getMessenger().getRequest(userId);
        player.getMessenger().declineRequest(requester);
    }
}

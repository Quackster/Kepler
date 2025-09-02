package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class MESSENGER_DECLINEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        boolean declineAll = reader.readBoolean();

        if (declineAll) {
            player.getMessenger().declineAllRequests();
            return;
        }

        int amount = reader.readInt();

        for (int i = 0; i < amount; i++) {
            int userId = reader.readInt();

            if (!player.getMessenger().hasRequest(userId)) {
                continue;
            }

            MessengerUser requester = player.getMessenger().getRequest(userId);
            player.getMessenger().declineRequest(requester);
        }
    }
}

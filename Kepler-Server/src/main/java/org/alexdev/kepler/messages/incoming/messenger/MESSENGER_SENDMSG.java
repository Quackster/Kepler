package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.messenger.INSTANT_MESSAGE_ERROR;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_MSG;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MESSENGER_SENDMSG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getVersion() < 23) {
            int amount = reader.readInt();

            List<Integer> friends = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                int friend_id = reader.readInt();
                friends.add(friend_id);
            }

            String chatMessage = StringUtil.filterInput(reader.readString(), false);

            for (int userId : friends) {
                int messageId = MessengerDao.newMessage(player.getDetails().getId(), userId, chatMessage);

                Player friend = PlayerManager.getInstance().getPlayerById(userId);

                if (friend != null) {
                    MessengerMessage message = new MessengerMessage(
                            messageId, userId, player.getDetails().getId(), DateUtil.getCurrentTimeSeconds(), chatMessage, null, null);

                    friend.send(new MESSENGER_MSG(message));
                }
            }
        } else {
            int userId = reader.readInt();
            String message = StringUtil.filterInput(reader.readString(), false);

            MessengerUser friend = player.getMessenger().getFriend(userId);

            if (friend == null) {
                player.send(new INSTANT_MESSAGE_ERROR(6, userId));
                return;
            }

            Player friendPlayer = PlayerManager.getInstance().getPlayerById(userId);

            if (friendPlayer == null) {
                player.send(new INSTANT_MESSAGE_ERROR(5, userId));
                return;
            }

            int messageId = MessengerDao.newMessage(player.getDetails().getId(), userId, message);

            MessengerMessage msg = new MessengerMessage(
                    messageId, userId, player.getDetails().getId(), DateUtil.getCurrentTimeSeconds(), message, null,null);

            friendPlayer.send(new MESSENGER_MSG(msg));
        }
    }
}

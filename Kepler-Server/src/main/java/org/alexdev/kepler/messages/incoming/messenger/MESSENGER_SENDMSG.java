package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
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
                        messageId, userId, player.getDetails().getId(), DateUtil.getCurrentTimeSeconds(), chatMessage);

                friend.send(new MESSENGER_MSG(message));
            }
        }
    }
}

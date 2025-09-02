package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.messenger.MessengerError;
import net.h4bbo.kepler.game.messenger.MessengerErrorType;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.messenger.BUDDY_REQUEST_RESULT;
import net.h4bbo.kepler.messages.outgoing.messenger.FRIEND_REQUEST;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_ERROR;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class MESSENGER_REQUESTBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String username = reader.readString();
        Messenger target = PlayerManager.getInstance().getMessengerData(username);

        if (target == null) {
            // Error type in external texts has it defined as "There was an error finding the user for the friend request"
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.FRIEND_REQUEST_NOT_FOUND)));
            return;
        }

        Messenger callee = player.getMessenger();

        if (callee.isFriendsLimitReached()) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.FRIENDLIST_FULL)));
            return;
        }

        if (target.hasFriend(player.getDetails().getId())) {
            return;
        }

        if (target.hasRequest(player.getDetails().getId())) {
            return;
        }

        if (target.isFriendsLimitReached()) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.TARGET_FRIEND_LIST_FULL)));
            return;
        }

        if (!target.isAllowsFriendRequests()) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.TARGET_DOES_NOT_ACCEPT)));
            return;
        }

        target.addRequest(callee.getMessengerUser());
    }
}

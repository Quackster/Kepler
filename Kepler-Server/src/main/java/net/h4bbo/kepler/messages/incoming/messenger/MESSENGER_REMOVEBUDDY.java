package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.messenger.MessengerError;
import net.h4bbo.kepler.game.messenger.MessengerErrorType;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_ERROR;
import net.h4bbo.kepler.messages.outgoing.messenger.REMOVE_FRIEND;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class MESSENGER_REMOVEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        // How many friends to remove?
        int size = reader.readInt();

        // List for removed friends
        List<MessengerUser> friendsRemoved = new ArrayList<>();

        // Friend instance, used later
        MessengerUser meAsFriend = player.getMessenger().getMessengerUser();

        // Remove all friends requested to be removed
        for (int i = 0; i < size; i++) {
            int friendId = reader.readInt();

            MessengerUser friend = player.getMessenger().getFriend(friendId);

            // If the clients requests a friend to be removed whom is not a friend, something has gone terribly wrong.
            if (!player.getMessenger().hasFriend(friendId)) {
                player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
                continue;
            }

            // Remove friend from our messenger
            if (!player.getMessenger().removeFriend(friendId)) {
                player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
                continue;
            }

            // Get messenger data of friend
            Messenger friendMessenger = PlayerManager.getInstance().getMessengerData(friendId);

            // Remove myself from friend's messenger
            if (!friendMessenger.removeFriend(meAsFriend.getUserId())) {
                player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
                continue;
            }

            // Get player instance of friend, will return null if not online
            Player playerFriend = PlayerManager.getInstance().getPlayerById(friendId);

            // If friend is online, send remove friend message
            if (playerFriend != null) {
                playerFriend.send(new REMOVE_FRIEND(meAsFriend));
            }

            // Add removed friend to list
            friendsRemoved.add(friend);
        }

        // Send list of removed friends
        player.send(new REMOVE_FRIEND(friendsRemoved));
    }
}

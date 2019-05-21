package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.game.messenger.*;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_ERROR;
import org.alexdev.kepler.messages.outgoing.messenger.REMOVE_BUDDY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_REMOVEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        // How many friends to remove?
        int size = reader.readInt();

        // List for removed friends
        //List<MessengerUser> friendsRemoved = new ArrayList<>();

        // Friend instance, used later
        MessengerUser meAsFriend = player.getMessenger().getMessengerUser();

        // Remove all friends requested to be removed
        //for (int i = 0; i < size; i++) {
        int friendId = reader.readInt();

        MessengerUser friend = player.getMessenger().getFriend(friendId);

        // If the clients requests a friend to be removed whom is not a friend, something has gone terribly wrong.
        if (!player.getMessenger().hasFriend(friendId)) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
            return;
        }

        // Remove friend from our messenger
        if (!player.getMessenger().removeFriend(friendId)) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
            return;
        }

        // Get messenger data of friend
        Messenger friendMessenger = MessengerManager.getInstance().getMessengerData(friendId);

        // Remove myself from friend's messenger
        if (!friendMessenger.removeFriend(meAsFriend.getUserId())) {
            player.send(new MESSENGER_ERROR(new MessengerError(MessengerErrorType.CONCURRENCY_ERROR)));
            return;
        }

        // Get player instance of friend, will return null if not online
        Player playerFriend = PlayerManager.getInstance().getPlayerById(friendId);

        // If friend is online, send remove friend message
        if (playerFriend != null) {
            playerFriend.send(new REMOVE_BUDDY(playerFriend, meAsFriend));
        }

        // Add removed friend to list
        // friendsRemoved.add(friend);
        //}

        // Send list of removed friends
        player.send(new REMOVE_BUDDY(player, friend));
    }
}

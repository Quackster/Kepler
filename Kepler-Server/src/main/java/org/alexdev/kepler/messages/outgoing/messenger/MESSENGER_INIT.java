package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.List;

public class MESSENGER_INIT extends PlayerMessageComposer {
    private final Player player;
    private String persistentMessage;
    private final int friendsLimit;
    private List<MessengerUser> friends;

    public MESSENGER_INIT(Player player, String persistentMessage, Messenger data) {
        this.player = player;
        this.persistentMessage = persistentMessage;
        this.friendsLimit = data.getFriendsLimit();
        this.friends = data.getFriends();
    }

    @Override
    public void compose(NettyResponse response) {
        int normalFriendsLimit = GameConfiguration.getInstance().getInteger("messenger.max.friends.nonclub");
        int clubFriendsLimit = GameConfiguration.getInstance().getInteger("messenger.max.friends.club");

        if (getPlayer().getVersion() < 23) {
            response.writeString(this.persistentMessage);
            response.writeInt(this.friendsLimit);
            response.writeInt(normalFriendsLimit);
            response.writeInt(clubFriendsLimit);

            response.writeInt(this.friends.size());

            for (MessengerUser friend : this.friends) {
                friend.serialise(player, response);
            }
        } else {
            response.writeInt(this.friendsLimit);
            response.writeInt(normalFriendsLimit);
            response.writeInt(clubFriendsLimit);
            response.writeInt(0);//this.player.getMessenger().getCategories().size());

            /*for (var category : this.player.getMessenger().getCategories()) {
                response.writeInt(category.getId());
                response.writeString(category.getName());
            }*/

            response.writeInt(this.friends.size());

            for (MessengerUser friend : this.friends) {
                friend.serialise(player, response);
            }

            response.writeInt(0);
            response.writeInt(0);
        }
    }

    @Override
    public short getHeader() {
        return 12; // "@L"
    }
}

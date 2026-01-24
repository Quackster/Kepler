package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.List;

public class MESSENGER_INIT extends MessageComposer {
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

        response.writeString(this.persistentMessage);
        response.writeInt(this.friendsLimit);
        response.writeInt(normalFriendsLimit);
        response.writeInt(clubFriendsLimit);

        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            friend.serialise(response);
        }
    }

    @Override
    public short getHeader() {
        return 12; // "@L"
    }
}

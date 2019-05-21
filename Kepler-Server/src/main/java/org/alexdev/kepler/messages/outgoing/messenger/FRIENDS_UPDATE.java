package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

public class FRIENDS_UPDATE extends MessageComposer {
    private final List<MessengerUser> friends;
    private final Messenger messenger;
    private final Player player;

    private List<MessengerUser> friendsAdded;
    private List<MessengerUser> friendsRemoved;
    private List<MessengerUser> friendsUpdated;

    public FRIENDS_UPDATE(Player player, Messenger messenger) {
        this.messenger = messenger;
        this.player = player;

        this.friends = new ArrayList<>();

        this.friendsAdded = new ArrayList<>();
        this.friendsRemoved = new ArrayList<>();
        this.friendsUpdated = new ArrayList<>();

        this.messenger.getFriendsAdd().drainTo(this.friendsAdded);
        this.messenger.getFriendsRemove().drainTo(this.friendsRemoved);
        this.messenger.getFriendsUpdate().drainTo(this.friendsUpdated);

        this.friends.addAll(this.friendsAdded);
        this.friends.addAll(this.friendsRemoved);
        this.friends.addAll(this.friendsUpdated);
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0); // TODO: Category count
        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            if (this.friendsRemoved.contains(friend)) {
                response.writeInt(-1);
                response.writeInt(friend.getUserId());
            } else {
                if (this.friendsAdded.contains(friend)) {
                    response.writeInt(1);
                }

                if (this.friendsUpdated.contains(friend)) {
                    response.writeInt(0);
                }

                friend.serialise(this.player, response);
            }
        }
    }

    @Override
    public short getHeader() {
        return 13; // "@M"
    }
}

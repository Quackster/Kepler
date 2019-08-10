package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

public class REMOVE_FRIEND extends PlayerMessageComposer {
    private final List<MessengerUser> friends;

    public REMOVE_FRIEND(MessengerUser friend) {
        this.friends = new ArrayList<>();
        this.friends.add(friend);
    }

    public REMOVE_FRIEND(List<MessengerUser> friends) {
        this.friends = friends;
    }

    @Override
    public void compose(NettyResponse response) {
        if (getPlayer().getVersion() < 23) {
            response.writeInt(this.friends.size());

            for (MessengerUser friend : this.friends) {
                response.writeInt(friend.getUserId());
            }
        } else {
            response.writeInt(0);
            response.writeInt(1);

            response.writeInt(-1);
            response.writeInt(friends.get(0).getUserId());
        }
    }

    @Override
    public short getHeader() {
        return (short) (getPlayer().getVersion() < 23 ? 138 : 13); // "BJ"
    }
}

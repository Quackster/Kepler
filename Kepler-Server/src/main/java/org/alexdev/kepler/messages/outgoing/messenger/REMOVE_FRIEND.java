package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

public class REMOVE_FRIEND extends MessageComposer {
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
        response.writeInt(this.friends.size());

        for (MessengerUser friend : this.friends) {
            response.writeInt(friend.getUserId());
        }
    }

    @Override
    public short getHeader() {
        return 138; // "BJ"
    }
}

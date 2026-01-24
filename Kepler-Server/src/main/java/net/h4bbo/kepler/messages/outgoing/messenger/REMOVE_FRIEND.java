package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

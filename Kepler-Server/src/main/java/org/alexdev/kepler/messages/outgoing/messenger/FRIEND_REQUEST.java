package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FRIEND_REQUEST extends MessageComposer {
    private final MessengerUser requester;

    public FRIEND_REQUEST(MessengerUser requester) {
        this.requester = requester;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.requester.getUserId());
        response.writeString(this.requester.getUsername());
    }

    @Override
    public short getHeader() {
        return 132; // "BD"
    }
}

package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

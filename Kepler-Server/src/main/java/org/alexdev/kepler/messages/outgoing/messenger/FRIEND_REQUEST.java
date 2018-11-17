package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FRIEND_REQUEST extends MessageComposer {
    private final int id;
    private final String name;

    public FRIEND_REQUEST(MessengerUser requester) {
        this.id = requester.getUserId();
        this.name = requester.getUsername();
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.id);
        response.writeString(this.name);
    }

    @Override
    public short getHeader() {
        return 132; // "BD"
    }
}

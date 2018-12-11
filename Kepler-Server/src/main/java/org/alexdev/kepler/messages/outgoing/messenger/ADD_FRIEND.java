package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ADD_FRIEND extends MessageComposer {
    private final MessengerUser friend;

    public ADD_FRIEND(MessengerUser friend) {
        this.friend = friend;
    }

    @Override
    public void compose(NettyResponse response) {
        this.friend.serialise(response);
    }

    @Override
    public short getHeader() {
        return 137; // "BI"
    }
}

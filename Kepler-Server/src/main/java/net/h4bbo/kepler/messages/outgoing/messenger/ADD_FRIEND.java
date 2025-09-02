package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ADD_FRIEND extends MessageComposer {
    private final MessengerUser friend;
    private final Player player;

    public ADD_FRIEND(MessengerUser friend, Player player) {
        this.friend = friend;
        this.player = player;
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

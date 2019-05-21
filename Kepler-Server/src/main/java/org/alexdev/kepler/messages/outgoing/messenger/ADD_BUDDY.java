package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ADD_BUDDY extends MessageComposer {
    private final MessengerUser friend;
    private final Player player;

    public ADD_BUDDY(Player player, MessengerUser friend) {
        this.friend = friend;
        this.player = player;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0);
        response.writeInt(1);

        response.writeInt(1);
        this.friend.serialise(this.player, response);
    }

    @Override
    public short getHeader() {
        return 13;
    }
}

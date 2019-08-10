package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ADD_FRIEND extends PlayerMessageComposer {
    private final MessengerUser friend;
    private final Player player;

    public ADD_FRIEND(MessengerUser friend, Player player) {
        this.friend = friend;
        this.player = player;
    }

    @Override
    public void compose(NettyResponse response) {
        if (getPlayer().getVersion() >= 23) {
            response.writeInt(0);
            response.writeInt(1);

            response.writeInt(1);
        }

        this.friend.serialise(getPlayer(), response);
    }

    @Override
    public short getHeader() {
        return (short) (getPlayer().getVersion() < 23 ? 137 : 13); // "BI"
    }
}

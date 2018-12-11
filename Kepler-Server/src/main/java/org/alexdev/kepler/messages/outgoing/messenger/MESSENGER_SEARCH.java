package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class MESSENGER_SEARCH extends MessageComposer {
    private final PlayerDetails details;

    public MESSENGER_SEARCH(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString("MESSENGER");

        if (this.details != null) {
            new MessengerUser(this.details).serialise(response);
        } else {
            response.writeInt(0);
        }
    }

    @Override
    public short getHeader() {
        return 128; // "B@"
    }
}

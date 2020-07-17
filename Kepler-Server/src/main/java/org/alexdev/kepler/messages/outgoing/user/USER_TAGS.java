package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_TAGS extends PlayerMessageComposer {
    private final PlayerDetails details;

    public USER_TAGS(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {

        response.writeInt(this.details.getId());
        response.writeInt(1);
        response.writeString("Webbanditten");

    }

    @Override
    public short getHeader() {
        return 350; // "??"
    }
}

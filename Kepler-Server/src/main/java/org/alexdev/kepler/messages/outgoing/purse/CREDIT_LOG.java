package org.alexdev.kepler.messages.outgoing.purse;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CREDIT_LOG extends MessageComposer {
    private final PlayerDetails details;

    public CREDIT_LOG(PlayerDetails details) {
        this.details = details;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeString("12/12/1994\t12:12\t333\t0\t\tstuff_store\r");
    }

    @Override
    public short getHeader() {
        return 209; // "CQ"
    }
}

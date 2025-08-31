package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CREDIT_BALANCE extends MessageComposer {
    private final int credits;

    public CREDIT_BALANCE(int credits) {
        this.credits = credits;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.credits + ".0");
    }

    @Override
    public short getHeader() {
        return 6; // "@F
    }
}

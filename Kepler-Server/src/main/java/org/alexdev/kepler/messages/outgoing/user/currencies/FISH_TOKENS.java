package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FISH_TOKENS extends MessageComposer {
    private final int fishBalance;

    public FISH_TOKENS(int fishBalance) {
        this.fishBalance = fishBalance;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(fishBalance);
    }

    @Override
    public short getHeader() {
        return 1102;
    }
}
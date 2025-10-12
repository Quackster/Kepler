package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class STAMP_BALANCE extends MessageComposer {
    private final int stampsBalance;

    public STAMP_BALANCE(int stampsBalance) {
        this.stampsBalance = stampsBalance;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(stampsBalance);
    }

    @Override
    public short getHeader() {
        return 628;
    }
}
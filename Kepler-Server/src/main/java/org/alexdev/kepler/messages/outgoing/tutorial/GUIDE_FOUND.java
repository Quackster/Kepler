package org.alexdev.kepler.messages.outgoing.tutorial;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GUIDE_FOUND extends MessageComposer {
    private final int accountId;

    public GUIDE_FOUND(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.accountId);
    }

    @Override
    public short getHeader() {
        return 423;
    }
}

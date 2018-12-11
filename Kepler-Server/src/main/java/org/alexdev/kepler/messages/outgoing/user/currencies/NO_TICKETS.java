package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class NO_TICKETS extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {

    }

    @Override
    public short getHeader() {
        return 73; // "AI"
    }
}

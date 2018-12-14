package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CRY_RECEIVED extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeString("H");
    }

    @Override
    public short getHeader() {
        return 321;
    }
}

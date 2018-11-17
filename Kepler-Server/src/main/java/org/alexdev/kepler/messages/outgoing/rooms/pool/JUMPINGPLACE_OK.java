package org.alexdev.kepler.messages.outgoing.rooms.pool;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class JUMPINGPLACE_OK extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {

    }

    @Override
    public short getHeader() {
        return 125; // "A}"
    }
}

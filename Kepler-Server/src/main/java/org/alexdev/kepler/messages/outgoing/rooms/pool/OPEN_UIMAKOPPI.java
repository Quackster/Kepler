package org.alexdev.kepler.messages.outgoing.rooms.pool;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class OPEN_UIMAKOPPI extends MessageComposer {

    @Override
    public void compose(NettyResponse response) {

    }

    @Override
    public short getHeader() {
        return 96; // "A`"
    }
}

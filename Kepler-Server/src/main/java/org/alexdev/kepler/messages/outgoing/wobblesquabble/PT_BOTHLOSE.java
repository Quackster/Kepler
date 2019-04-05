package org.alexdev.kepler.messages.outgoing.wobblesquabble;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PT_BOTHLOSE extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
    }

    @Override
    public short getHeader() {
        return 120;
    }
}

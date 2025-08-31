package org.alexdev.kepler.messages.outgoing.tutorial;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INVITATION_SENT extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {

    }

    @Override
    public short getHeader() {
        return 421; // "Fe"
    }
}

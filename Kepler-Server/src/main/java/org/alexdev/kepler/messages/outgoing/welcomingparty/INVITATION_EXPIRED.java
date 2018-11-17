package org.alexdev.kepler.messages.outgoing.welcomingparty;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INVITATION_EXPIRED extends MessageComposer {
    public INVITATION_EXPIRED() {

    }

    @Override
    public void compose(NettyResponse response) {
        // Packet doesn't have any packet structure according to Lingo
    }

    @Override
    public short getHeader() {
        return 360; // "Eh"
    }
}

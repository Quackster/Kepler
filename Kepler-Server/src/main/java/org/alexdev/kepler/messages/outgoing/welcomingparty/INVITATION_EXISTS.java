package org.alexdev.kepler.messages.outgoing.welcomingparty;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INVITATION_EXISTS extends MessageComposer {
    public INVITATION_EXISTS() {

    }

    @Override
    public void compose(NettyResponse response) {
        // Packet doesn't have any packet structure according to Lingo
    }

    @Override
    public short getHeader() {
        return 358; // "Ef"
    }
}

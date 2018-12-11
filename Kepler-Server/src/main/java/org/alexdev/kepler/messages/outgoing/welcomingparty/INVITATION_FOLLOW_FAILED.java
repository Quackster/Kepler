package org.alexdev.kepler.messages.outgoing.welcomingparty;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INVITATION_FOLLOW_FAILED extends MessageComposer {
    public INVITATION_FOLLOW_FAILED() {

    }

    @Override
    public void compose(NettyResponse response) {
        // Packet doesn't have any packet structure according to Lingo
    }

    @Override
    public short getHeader() {
        return 359; // "Eg"
    }
}

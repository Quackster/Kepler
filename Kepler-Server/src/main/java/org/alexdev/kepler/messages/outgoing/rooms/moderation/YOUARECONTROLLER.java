package org.alexdev.kepler.messages.outgoing.rooms.moderation;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class YOUARECONTROLLER extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {

    }

    @Override
    public short getHeader() {
        return 42; // "@j"
    }
}

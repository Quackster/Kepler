package org.alexdev.kepler.messages.outgoing.moderation;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_BANNED extends MessageComposer {
    private final String banReason;

    public USER_BANNED(String banReason) {
        this.banReason = banReason;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.banReason);
    }

    @Override
    public short getHeader() {
        return 35; // "@c"
    }
}

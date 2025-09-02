package net.h4bbo.kepler.messages.outgoing.moderation;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

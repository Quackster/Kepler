package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class INVITATION_ERROR extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0);
    }

    @Override
    public short getHeader() {
        return 262;
    }
}

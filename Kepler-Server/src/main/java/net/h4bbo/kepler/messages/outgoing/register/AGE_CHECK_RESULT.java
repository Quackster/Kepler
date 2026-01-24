package net.h4bbo.kepler.messages.outgoing.register;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class AGE_CHECK_RESULT extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeBool(true);
    }

    @Override
    public short getHeader() {
        return 164;
    }
}

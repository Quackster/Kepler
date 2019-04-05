package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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

package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CUSTOM_FILTER_RESULT extends MessageComposer {
    private final int result;

    public CUSTOM_FILTER_RESULT(int result) {
        this.result = result;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(result);
    }

    @Override
    public short getHeader() {
        return 217;
    }
}

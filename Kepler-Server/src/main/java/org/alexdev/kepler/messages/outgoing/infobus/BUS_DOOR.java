package org.alexdev.kepler.messages.outgoing.infobus;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class BUS_DOOR extends MessageComposer {
    private final boolean status;

    public BUS_DOOR(boolean status) {
        this.status = status;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.status);
    }

    @Override
    public short getHeader() {
        return 503;
    }
}

package net.h4bbo.kepler.messages.outgoing.infobus;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

package net.h4bbo.kepler.messages.outgoing.catalogue;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ALIAS_TOGGLE extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeBool(false);
    }

    @Override
    public short getHeader() {
        return 297; // "Di"
    }
}

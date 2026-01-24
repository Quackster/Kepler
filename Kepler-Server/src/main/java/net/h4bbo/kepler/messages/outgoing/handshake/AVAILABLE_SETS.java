package net.h4bbo.kepler.messages.outgoing.handshake;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class AVAILABLE_SETS extends MessageComposer {
    private final String set;

    public AVAILABLE_SETS(String set) {
        this.set = set;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.set);
    }

    @Override
    public short getHeader() {
        return 8; // "@H"
    }
}

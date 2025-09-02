package net.h4bbo.kepler.messages.outgoing.user;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class LATENCY extends MessageComposer {
    private final int latency;

    public LATENCY(int latency) {
        this.latency = latency;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.latency);
    }

    @Override
    public short getHeader() {
        return 354; // "Eb"
    }
}

package net.h4bbo.kepler.messages.outgoing.handshake;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SECRET_KEY extends MessageComposer {
    private final String key;

    public SECRET_KEY(String key) {
        this.key = key;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.key);
    }

    @Override
    public short getHeader() {
        return 1; // "@A"
    }
}

package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class DATE extends MessageComposer {
    private final String shortDate;

    public DATE(String shortDate) {
        this.shortDate = shortDate;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.shortDate);
    }

    @Override
    public short getHeader() {
        return 163; // "Bc"
    }
}

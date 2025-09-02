package net.h4bbo.kepler.messages.outgoing.alert;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class NO_USER_FOUND extends MessageComposer {
    private final String username;

    public NO_USER_FOUND(String username) {
        this.username = username;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.username);
    }

    @Override
    public short getHeader() {
        return 76; // "AL"
    }
}

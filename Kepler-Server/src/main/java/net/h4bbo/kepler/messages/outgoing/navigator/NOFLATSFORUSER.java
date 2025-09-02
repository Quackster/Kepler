package net.h4bbo.kepler.messages.outgoing.navigator;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class NOFLATSFORUSER extends MessageComposer {
    private String username;

    public NOFLATSFORUSER(String username) {
        this.username = username;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.username);
    }

    @Override
    public short getHeader() {
        return 57; // "@y"
    }
}

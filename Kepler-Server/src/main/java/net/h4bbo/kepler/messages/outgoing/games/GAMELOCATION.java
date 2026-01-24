package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class GAMELOCATION extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeInt(-1);
    }

    @Override
    public short getHeader() {
        return 241;
    }
}

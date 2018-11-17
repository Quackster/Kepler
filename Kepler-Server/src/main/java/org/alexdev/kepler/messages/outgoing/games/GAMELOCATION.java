package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GAMELOCATION extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeInt(-1);
    }

    @Override
    public short getHeader() {
        return 241; // "Cq"
    }
}

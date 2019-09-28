package org.alexdev.kepler.messages.outgoing.recycler;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class RECYCLER_STATUS extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0);//tStatus = tConn.GetIntFrom()
    }

    @Override
    public short getHeader() {
        return 304; // "Dp"
    }
}

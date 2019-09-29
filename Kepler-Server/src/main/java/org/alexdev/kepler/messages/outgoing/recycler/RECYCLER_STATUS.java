package org.alexdev.kepler.messages.outgoing.recycler;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class RECYCLER_STATUS extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0);//tStatus = tConn.GetIntFrom()
        //response.writeInt(2);//tStatus = tConn.GetIntFrom()
        //response.writeInt(0);
        //response.writeString("sound_set_9");
    }

    @Override
    public short getHeader() {
        return 304; // "Dp"
    }
}

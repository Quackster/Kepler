package org.alexdev.kepler.messages.outgoing.recycler;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class RECYCLER_CONFIGURATION extends MessageComposer {
    @Override
    public void compose(NettyResponse response) {
        response.writeBool(false);//tServiceEnabled = tConn.GetIntFrom()
        response.writeInt(10);//tQuarantineMinutes = tConn.GetIntFrom()
        response.writeInt(20);//tRecyclingMinutes = tConn.GetIntFrom()
        response.writeInt(30);//tMinutesToTimeout = tConn.GetIntFrom()
        response.writeInt(0);//tNumOfRewardItems = tConn.GetIntFrom()
    }

    @Override
    public short getHeader() {
        return 303; // "Do"
    }
}

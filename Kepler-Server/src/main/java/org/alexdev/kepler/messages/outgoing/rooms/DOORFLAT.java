package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class DOORFLAT extends MessageComposer {
    private final int teleId;
    private final int flatID;

    public DOORFLAT(int teleId, int flatId) {
        this.teleId = teleId;
        this.flatID = flatId;
    }

    @Override
    public void compose(NettyResponse response) {
        System.out.println("TELEID: " + teleId + "; FLATID: " + flatID);
        response.writeInt(this.teleId);
        response.writeInt(this.flatID);
    }

    @Override
    public short getHeader() {
        return 62; // "@~"
    }
}

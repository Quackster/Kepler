package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PLAYERREJOINED extends MessageComposer {
    private final int instanceId;

    public PLAYERREJOINED(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.instanceId);
    }

    @Override
    public short getHeader() {
        return 245; // "Cu"
    }
}

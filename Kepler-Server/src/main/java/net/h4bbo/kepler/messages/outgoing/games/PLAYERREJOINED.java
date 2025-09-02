package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

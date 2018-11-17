package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class LOGOUT extends MessageComposer {
    private final int instanceId;

    public LOGOUT(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.instanceId);
    }

    @Override
    public short getHeader() {
        return 29; // "@]
    }
}

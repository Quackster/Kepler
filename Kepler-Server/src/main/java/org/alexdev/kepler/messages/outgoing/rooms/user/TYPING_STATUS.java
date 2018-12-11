package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class TYPING_STATUS extends MessageComposer {
    private final int instanceId;
    private final boolean typing;

    public TYPING_STATUS(int instanceId, boolean typing) {
        this.instanceId = instanceId;
        this.typing = typing;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.instanceId);
        response.writeBool(this.typing);
    }

    @Override
    public short getHeader() {
        return 361; // "Ei"
    }
}

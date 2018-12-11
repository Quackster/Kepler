package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FOLLOW_ERROR extends MessageComposer {
    private final int errorId;

    public FOLLOW_ERROR(int errorId) {
        this.errorId = errorId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.errorId);
    }

    @Override
    public short getHeader() {
        return 349; // "E]"
    }
}

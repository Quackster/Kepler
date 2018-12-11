package org.alexdev.kepler.messages.outgoing.rooms.settings;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FLATCAT extends MessageComposer {
    private final int roomId;
    private final int categoryId;

    public FLATCAT(int roomId, int categoryId) {
        this.roomId = roomId;
        this.categoryId = categoryId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.roomId);
        response.writeInt(this.categoryId);
    }

    @Override
    public short getHeader() {
        return 222; // "C^"
    }
}

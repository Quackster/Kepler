package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ROOM_READY extends MessageComposer {
    private final int roomId;
    private final String model;

    public ROOM_READY(int roomId, String model) {
        this.roomId = roomId;
        this.model = model;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.model);
        response.writeString(" ");
        response.writeInt(this.roomId);
    }

    @Override
    public short getHeader() {
        return 69; // "AE"
    }
}

package net.h4bbo.kepler.messages.outgoing.rooms.items;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class TELEPORTER_INIT extends MessageComposer {
    private final int teleporterId;
    private final int roomId;

    public TELEPORTER_INIT(int teleporterId, int roomId) {
        this.teleporterId = teleporterId;
        this.roomId = roomId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.teleporterId);
        response.writeInt(this.roomId);
    }

    @Override
    public short getHeader() {
        return 62; // "@~"
    }
}

package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ROOMFORWARD extends MessageComposer {
    private final boolean isPublic;
    private int roomId;

    public ROOMFORWARD(boolean isPublic, int roomId) {
        this.isPublic = isPublic;
        this.roomId = roomId;

        if (this.isPublic) {
            this.roomId = this.roomId + RoomManager.PUBLIC_ROOM_OFFSET;
        }
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.isPublic);
        response.writeInt(this.roomId);
    }

    @Override
    public short getHeader() {
        return 286; // "D^"
    }
}

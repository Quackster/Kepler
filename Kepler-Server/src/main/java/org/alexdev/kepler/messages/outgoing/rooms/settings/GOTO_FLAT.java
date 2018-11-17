package org.alexdev.kepler.messages.outgoing.rooms.settings;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GOTO_FLAT extends MessageComposer {
    private final int roomId;
    private final String roomName;

    public GOTO_FLAT(int roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeDelimeter(this.roomId, (char)13);
        response.write(this.roomName);
    }

    @Override
    public short getHeader() {
        return 59; // "@{"
    }
}

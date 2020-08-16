package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GROUP_MEMBERSHIP_UPDATE extends MessageComposer {

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(1);
        response.writeInt(1);
        response.writeInt(1);

    }

    @Override
    public short getHeader() {
        return 310; // "Dv"
    }
}

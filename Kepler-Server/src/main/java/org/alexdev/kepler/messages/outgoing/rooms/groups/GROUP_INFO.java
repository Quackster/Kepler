package org.alexdev.kepler.messages.outgoing.rooms.groups;

import org.alexdev.kepler.game.groups.Group;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.HashMap;

public class GROUP_INFO extends MessageComposer {

    private final Group group;

    public GROUP_INFO(Group group) {
        this.group = group;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.group.getId());
        response.writeString(this.group.getName());
        response.writeString(this.group.getDescription());
    }

    @Override
    public short getHeader() {
        return 311;
    }
}

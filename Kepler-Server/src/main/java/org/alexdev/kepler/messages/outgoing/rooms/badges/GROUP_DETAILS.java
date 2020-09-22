package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.group.Group;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GROUP_DETAILS extends MessageComposer {

    private Group group;
    public GROUP_DETAILS(Group group) {
        this.group = group;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(group.getId());
        response.writeString((group.getName() != null) ? group.getName() : "");
        response.writeString((group.getDescription() != null) ? group.getDescription() : "");

    }

    @Override
    public short getHeader() {
        return 311; // "Dw"
    }
}

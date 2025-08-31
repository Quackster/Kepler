package org.alexdev.kepler.messages.outgoing.rooms.groups;

import org.alexdev.kepler.game.groups.Group;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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

        var room = RoomManager.getInstance().getRoomById(this.group.getRoomId());

        if (this.group.getRoomId() > 0 && room != null) {
            response.writeInt(this.group.getRoomId());
            response.writeString(room.getData().getName());
        } else {
            response.writeInt(-1);
            response.writeString("");
        }
    }

    @Override
    public short getHeader() {
        return 311;
    }
}

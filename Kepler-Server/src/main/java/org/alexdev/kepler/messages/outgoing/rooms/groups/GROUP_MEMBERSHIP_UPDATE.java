package org.alexdev.kepler.messages.outgoing.rooms.groups;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GROUP_MEMBERSHIP_UPDATE extends MessageComposer {
    private final int instanceId;
    private final int groupId;
    private final int rankId;

    public GROUP_MEMBERSHIP_UPDATE(int instanceId, int groupId, int rankId) {
        this.instanceId = instanceId;
        this.groupId = groupId;
        this.rankId = rankId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.instanceId);
        response.writeInt(this.groupId);
        response.writeInt(this.rankId);
    }

    @Override
    public short getHeader() {
        return 310;
    }
}
package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class TAG_LIST extends MessageComposer {
    private final List<String> tags;
    private final int userId;

    public TAG_LIST(int userId, List<String> tags) {
        this.userId = userId;
        this.tags = tags;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.userId);
        response.writeInt(this.tags.size());

        for (String tag : this.tags) {
            response.writeString(tag);
        }
    }

    @Override
    public short getHeader() {
        return 350;
    }
}

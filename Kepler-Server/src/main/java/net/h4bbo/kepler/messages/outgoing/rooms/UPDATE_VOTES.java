package net.h4bbo.kepler.messages.outgoing.rooms;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class UPDATE_VOTES extends MessageComposer {
    private final int rating;

    public UPDATE_VOTES(int rating) {
        this.rating = rating;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.rating);
    }

    @Override
    public short getHeader() {
        return 345;
    }
}

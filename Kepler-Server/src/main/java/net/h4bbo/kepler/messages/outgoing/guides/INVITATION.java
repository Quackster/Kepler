package net.h4bbo.kepler.messages.outgoing.guides;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class INVITATION extends MessageComposer {
    private final Integer userId;
    private final String username;

    public INVITATION(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.userId);
        response.writeString(this.username);
    }

    @Override
    public short getHeader() {
        return 355; // "Ec"
    }
}

package net.h4bbo.kepler.messages.outgoing.moderation;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class CRY_REPLY extends MessageComposer {
    private final String message;

    public CRY_REPLY(String message){
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.message);
    }

    @Override
    public short getHeader() {
        return 274; // "DR"
    }
}

package net.h4bbo.kepler.messages.outgoing.moderation;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class MODERATOR_ALERT extends MessageComposer {
    private final String message;

    public MODERATOR_ALERT(String message){
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString("mod_warn/"+message);
    }

    @Override
    public short getHeader() {
        return 33;
    }
}

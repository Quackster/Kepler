package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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

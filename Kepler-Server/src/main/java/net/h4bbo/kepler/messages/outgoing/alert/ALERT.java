package net.h4bbo.kepler.messages.outgoing.alert;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ALERT extends MessageComposer {
    private final String message;

    public ALERT(String message) {
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.message);
    }

    @Override
    public short getHeader() {
        return 139; // "BK"
    }
}

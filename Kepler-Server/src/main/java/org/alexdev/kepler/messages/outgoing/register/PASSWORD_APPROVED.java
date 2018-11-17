package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PASSWORD_APPROVED extends MessageComposer {
    private final int errorCode;

    public PASSWORD_APPROVED(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.errorCode);
    }

    @Override
    public short getHeader() {
        return 282; // "DZ"
    }
}

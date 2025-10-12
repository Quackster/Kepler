package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class REGISTRATION_OK extends MessageComposer {
    private final String totpCode;

    public REGISTRATION_OK(String totpCode) {
        this.totpCode = totpCode;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.totpCode);
    }

    @Override
    public short getHeader() {
        return 51;
    }
}

package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class TOTP_EMAIL_VERIFICATION_REQUIRED extends MessageComposer {
    private final boolean required;

    public TOTP_EMAIL_VERIFICATION_REQUIRED(boolean required) {
        this.required = required;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(required ? 1 : 0);
    }

    @Override
    public short getHeader() {
        return 1575;
    }
}

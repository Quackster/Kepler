package org.alexdev.kepler.messages.outgoing.register;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class APPROVENAMERELY extends MessageComposer {
    private final int nameCheckCode;

    public APPROVENAMERELY(int nameCheckCode) {
        this.nameCheckCode = nameCheckCode;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.nameCheckCode);
    }

    @Override
    public short getHeader() {
        return 36;
    }
}

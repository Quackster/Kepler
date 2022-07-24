package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class UPDATE_ACCOUNT extends MessageComposer {

    private final int response;

    public UPDATE_ACCOUNT(int response) {
        this.response = response;
    }

    @Override
    public void compose(NettyResponse response) {

        response.writeInt(this.response);
    }

    @Override
    public short getHeader() {
        return 169;
    }
}

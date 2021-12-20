package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class UPDATE_ACCOUNT extends PlayerMessageComposer {

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

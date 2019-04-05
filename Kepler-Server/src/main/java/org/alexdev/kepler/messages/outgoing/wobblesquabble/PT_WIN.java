package org.alexdev.kepler.messages.outgoing.wobblesquabble;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PT_WIN extends MessageComposer {
    private final int loser;

    public PT_WIN(int winner) {
        this.loser = winner;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.loser);
    }

    @Override
    public short getHeader() {
        return 119;
    }
}

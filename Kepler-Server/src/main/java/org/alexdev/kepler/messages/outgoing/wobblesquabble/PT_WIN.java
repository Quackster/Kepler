package org.alexdev.kepler.messages.outgoing.wobblesquabble;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PT_WIN extends MessageComposer {
    private final int winner;

    public PT_WIN(int winner) {
        this.winner = winner;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.winner == -1) {
            response.writeInt(0);
        } else if (this.winner == 1) {
            response.writeInt(-1);
        } else if (this.winner == 0) {
            response.writeInt(1);
        }
    }

    @Override
    public short getHeader() {
        return 119;
    }
}

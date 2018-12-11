package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GAMEDELETED extends MessageComposer {
    private int gameId;

    public GAMEDELETED(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.gameId);
    }

    @Override
    public short getHeader() {
        return 237; // "Cm"
    }
}

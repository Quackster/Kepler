package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

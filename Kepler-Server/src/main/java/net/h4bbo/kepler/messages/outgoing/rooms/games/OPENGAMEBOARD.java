package net.h4bbo.kepler.messages.outgoing.rooms.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class OPENGAMEBOARD extends MessageComposer {
    private final String gameId;
    private final String type;

    public OPENGAMEBOARD(String gameId, String type) {
        this.gameId = gameId;
        this.type = type;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeDelimeter(this.gameId, (char)9);
        response.writeDelimeter(this.type, (char)9);
        //response.writeDelimeter("xo", (char)9);
    }

    @Override
    public short getHeader() {
        return 145; // "BQ"
    }
}

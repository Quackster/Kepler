package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class GAMESTART extends MessageComposer {
    private final int gameLengthSeconds;

    public GAMESTART(int gameLengthSeconds) {
        this.gameLengthSeconds = gameLengthSeconds;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.gameLengthSeconds);
    }

    @Override
    public short getHeader() {
        return 247;
    }
}

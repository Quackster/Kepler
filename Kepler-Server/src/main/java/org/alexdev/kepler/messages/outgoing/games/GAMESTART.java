package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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
        return 247; // "Cw"
    }
}

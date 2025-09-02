package net.h4bbo.kepler.messages.outgoing.songs;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SONG_NEW extends MessageComposer {
    private final int itemId;
    private final String title;

    public SONG_NEW(int id, String title) {
        this.itemId = id;
        this.title = title;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.itemId);
        response.writeString(this.title);
    }

    @Override
    public short getHeader() {
        return 331; // "EK"
    }
}

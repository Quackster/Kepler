package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ROOM_AD extends MessageComposer {
    private final String img;
    private final String url;
    public ROOM_AD() {
        this.img = null;
        this.url = null;
    }
    public ROOM_AD(String img, String url) {
        this.img = img;
        this.url = url;
    }
    @Override
    public void compose(NettyResponse response) {
        if(this.img != null && this.url != null) {
            response.writeString(this.img+"\t"+this.url+"\r");
        } else {
            response.writeInt(0);
        }
    }

    @Override
    public short getHeader() {
        return 208; // "CP"
    }
}

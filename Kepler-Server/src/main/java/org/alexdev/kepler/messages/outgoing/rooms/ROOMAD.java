package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ROOMAD extends MessageComposer {
    private final String img;
    private final String url;
    public ROOMAD() {
        this.img = null;
        this.url = null;
    }
    public ROOMAD(String img, String url) {
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

package net.h4bbo.kepler.messages.outgoing.moderation;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class DELETE_CRY extends MessageComposer {
    private int cryId;

    public DELETE_CRY(int cryId){
        this.cryId = cryId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(cryId);
    }

    @Override
    public short getHeader() {
        return 273; // "DQ"
    }
}

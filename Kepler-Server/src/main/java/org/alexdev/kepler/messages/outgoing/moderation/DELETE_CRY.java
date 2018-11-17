package org.alexdev.kepler.messages.outgoing.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class DELETE_CRY extends MessageComposer {
    private CallForHelp cfh;

    public DELETE_CRY(CallForHelp cfh){
        this.cfh = cfh;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.cfh.getCallId());
    }

    @Override
    public short getHeader() {
        return 273; // "DQ"
    }
}

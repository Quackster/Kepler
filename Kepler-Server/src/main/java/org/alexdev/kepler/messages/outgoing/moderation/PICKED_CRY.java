package org.alexdev.kepler.messages.outgoing.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PICKED_CRY extends MessageComposer {

    private CallForHelp cfh;

    public PICKED_CRY(CallForHelp cfh){
        this.cfh = cfh;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(cfh.getCallId());
        response.writeString(cfh.getPickedUpBy());
    }

    @Override
    public short getHeader() {
        return 149; // "BU"
    }
}

package org.alexdev.kepler.messages.outgoing.tutorial;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INVITING_COMPLETED extends MessageComposer {
    public enum InvitationResult {
        SUCCESS,
        FAILURE;
    }

    private InvitationResult result;

    public INVITING_COMPLETED(InvitationResult result) {
        this.result = result;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.result == InvitationResult.SUCCESS) {
            response.writeBool(true);
        } else {
            response.writeBool(false);
        }
    }

    @Override
    public short getHeader() {
        return 357; // "Ee"
    }
}


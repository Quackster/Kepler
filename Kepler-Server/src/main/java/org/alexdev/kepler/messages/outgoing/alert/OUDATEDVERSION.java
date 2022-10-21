package org.alexdev.kepler.messages.outgoing.alert;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class OUDATEDVERSION extends MessageComposer {

    private boolean required;

    public OUDATEDVERSION(boolean required) {
        this.required = required;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.required ? "required" : "optional");
    }

    @Override
    public short getHeader() {
        return 4;
    }
}

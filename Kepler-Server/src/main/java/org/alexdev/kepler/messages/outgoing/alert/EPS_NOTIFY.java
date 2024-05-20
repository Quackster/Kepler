package org.alexdev.kepler.messages.outgoing.alert;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class EPS_NOTIFY extends MessageComposer {

    private String text = "";
    public EPS_NOTIFY() {
    }
    public EPS_NOTIFY(String text) {
        this.text = text;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeValue("t", 580);
        response.writeValue("p", this.text);
    }

    @Override
    public short getHeader() {
        return 52;
    }
}

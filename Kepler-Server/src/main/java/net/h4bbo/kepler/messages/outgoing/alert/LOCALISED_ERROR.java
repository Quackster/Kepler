package net.h4bbo.kepler.messages.outgoing.alert;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class LOCALISED_ERROR extends MessageComposer {

    private final String externalTextEntry;

    public LOCALISED_ERROR(String externalTextEntry) {
        this.externalTextEntry = externalTextEntry;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.externalTextEntry);
    }

    @Override
    public short getHeader() {
        return 33; // "@a"
    }
}

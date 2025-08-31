package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FLATPROPERTY extends MessageComposer {
    private final String property;
    private final String value;

    public FLATPROPERTY(String property, Object value) {
        this.property = property;
        this.value = String.valueOf(value);
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(this.property);
        response.write("/");
        response.write(this.value);
    }

    @Override
    public short getHeader() {
        return 46; // "@n"
    }
}

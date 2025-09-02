package net.h4bbo.kepler.messages.outgoing.rooms.items;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SHOWPROGRAM extends MessageComposer {
    private final String[] arguments;
    public SHOWPROGRAM(String[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public void compose(NettyResponse response) {
        response.write(String.join(" ", this.arguments));
    }

    @Override
    public short getHeader() {
        return 71; // "AG"
    }
}

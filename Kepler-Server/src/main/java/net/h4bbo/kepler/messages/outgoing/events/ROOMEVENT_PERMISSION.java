package net.h4bbo.kepler.messages.outgoing.events;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ROOMEVENT_PERMISSION extends MessageComposer {
    private final boolean canCreateEvent;

    public ROOMEVENT_PERMISSION(boolean canCreateEvent) {
        this.canCreateEvent = canCreateEvent;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.canCreateEvent);
    }

    @Override
    public short getHeader() {
        return 367; // "Eo"
    }
}

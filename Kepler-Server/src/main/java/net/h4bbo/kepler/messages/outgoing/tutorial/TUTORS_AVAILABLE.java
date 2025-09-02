package net.h4bbo.kepler.messages.outgoing.tutorial;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class TUTORS_AVAILABLE extends MessageComposer {
    private final int tutors;

    public TUTORS_AVAILABLE(int tutors) {
        this.tutors = tutors;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.tutors);
    }

    @Override
    public short getHeader() {
        return 356; // "Ed"
    }
}

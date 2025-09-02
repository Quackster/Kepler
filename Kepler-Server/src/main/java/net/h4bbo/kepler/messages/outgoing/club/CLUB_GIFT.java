package net.h4bbo.kepler.messages.outgoing.club;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class CLUB_GIFT extends MessageComposer {
    private final int giftCount;

    public CLUB_GIFT(int giftCount) {
        this.giftCount = giftCount;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.giftCount);
    }

    @Override
    public short getHeader() {
        return 280;
    }
}

package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class TRADING_STATUS_UPDATE extends MessageComposer {
    private final int tradingStatus;

    public TRADING_STATUS_UPDATE(int tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(tradingStatus);
    }

    @Override
    public short getHeader() {
        return 1001;
    }
}

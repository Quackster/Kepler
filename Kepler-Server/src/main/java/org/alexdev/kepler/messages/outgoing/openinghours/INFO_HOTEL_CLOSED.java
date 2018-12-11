package org.alexdev.kepler.messages.outgoing.openinghours;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.time.LocalTime;

public class INFO_HOTEL_CLOSED extends MessageComposer {
    private final LocalTime openTime;

    // Denotes if the ugly popup is used or the more fancy one
    private final boolean disconnect;

    public INFO_HOTEL_CLOSED(LocalTime openTime, boolean disconnect) {
        this.openTime = openTime;
        this.disconnect = disconnect;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.openTime.getHour());
        response.writeInt(this.openTime.getMinute());
        response.writeBool(this.disconnect);
    }

    @Override
    public short getHeader() {
        return 292; // "Dd"
    }
}
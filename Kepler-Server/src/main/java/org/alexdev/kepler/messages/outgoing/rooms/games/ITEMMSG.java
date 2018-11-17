package org.alexdev.kepler.messages.outgoing.rooms.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ITEMMSG extends MessageComposer {
    private final String[] commands;

    public ITEMMSG(String[] commands) {
        this.commands = commands;
    }

    @Override
    public void compose(NettyResponse response) {
        for (String value : this.commands) {
            response.writeDelimeter(value, '\r');
        }
    }

    @Override
    public short getHeader() {
        return 144; // "BP"
    }
}

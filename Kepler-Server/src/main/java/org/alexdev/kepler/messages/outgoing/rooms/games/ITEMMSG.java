package org.alexdev.kepler.messages.outgoing.rooms.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ITEMMSG extends MessageComposer {
    private final String[] commands;
    private final boolean delimitate;

    /**
     * Create a new ITEMMSG with multiple commands.
     * Each line will be delimited with '\r'
     * @param commands
     */
    public ITEMMSG(String[] commands) {
        this.commands = commands;
        this.delimitate = true;
    }

    /**
     * Create a new ITEMMSG with a single command.
     * This expects the command to be already delimited
     * @param command
     */
    public ITEMMSG(String command) {
        this.commands = new String[]{command};
        this.delimitate = false;
    }

    @Override
    public void compose(NettyResponse response) {
        for (String value : this.commands) {
            if (this.delimitate)
                response.writeDelimeter(value, '\r');
            else
                response.write(value);
        }
    }

    @Override
    public short getHeader() {
        return 144; // "BP"
    }
}

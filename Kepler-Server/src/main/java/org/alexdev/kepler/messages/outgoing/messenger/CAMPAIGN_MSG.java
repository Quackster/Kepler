package org.alexdev.kepler.messages.outgoing.messenger;

import org.alexdev.kepler.game.messenger.MessengerMessage;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CAMPAIGN_MSG extends MessageComposer {
    private final MessengerMessage message;

    public CAMPAIGN_MSG(MessengerMessage message) {
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.message.getId());
        response.writeString(this.message.getLink());
        response.writeString(this.message.getUrl());
        response.writeString(this.message.getMessage());
    }

    @Override
    public short getHeader() {
        return 133; // "BE"
    }
}

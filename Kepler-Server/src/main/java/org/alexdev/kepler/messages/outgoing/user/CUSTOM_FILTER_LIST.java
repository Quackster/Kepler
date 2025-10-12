package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.Set;

public class CUSTOM_FILTER_LIST extends MessageComposer {
    private final Set<String> phrases;

    public CUSTOM_FILTER_LIST(Set<String> phrases) {
        this.phrases = phrases;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(phrases.size());
        for (String phrase : phrases) {
            response.writeString(phrase);
        }
    }

    @Override
    public short getHeader() {
        return 216;
    }
}

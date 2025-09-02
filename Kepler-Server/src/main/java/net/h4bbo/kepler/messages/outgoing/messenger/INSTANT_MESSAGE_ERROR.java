package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class INSTANT_MESSAGE_ERROR extends MessageComposer {
    private int errorCode;
    private int chatId;

    public INSTANT_MESSAGE_ERROR(int errorCode, int chatId) {
        this.errorCode = errorCode;
        this.chatId = chatId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.errorCode);
        response.writeInt(this.chatId);
    }

    @Override
    public short getHeader() {
        return 261; // "DE"
    }
}
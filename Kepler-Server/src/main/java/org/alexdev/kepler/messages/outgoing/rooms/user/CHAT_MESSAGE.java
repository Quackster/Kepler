package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CHAT_MESSAGE extends MessageComposer {
    public enum ChatMessageType {
        CHAT (24), // @X
        SHOUT (26), // @Z
        WHISPER (25); // @Y

        private final short header;

        ChatMessageType(int header) {
            this.header = (short) header;
        }

        public short getHeader() {
            return header;
        }
    }

    private final ChatMessageType type;
    private final int instanceId;
    private final String message;

    public CHAT_MESSAGE(ChatMessageType type, int instanceId, String message) {
        this.type = type;
        this.instanceId = instanceId;
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.instanceId);
        response.writeString(this.message);
    }

    @Override
    public short getHeader() {
        return this.type.getHeader();
    }
}

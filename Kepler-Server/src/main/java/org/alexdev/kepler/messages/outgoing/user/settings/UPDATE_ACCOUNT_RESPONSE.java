package org.alexdev.kepler.messages.outgoing.user.settings;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class UPDATE_ACCOUNT_RESPONSE extends MessageComposer {
    public enum ResponseType {
        SUCCESS(0),
        INCORRECT_PASSWORD(1),
        INCORRECT_BIRTHDAY(2);

        private final int statusId;

        ResponseType(int statusId) {
            this.statusId = statusId;
        }

        public int getStatusId() {
            return statusId;
        }
    }

    public ResponseType responseType;

    public UPDATE_ACCOUNT_RESPONSE(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.responseType.statusId);
    }

    @Override
    public short getHeader() {
        return 169;
    }
}

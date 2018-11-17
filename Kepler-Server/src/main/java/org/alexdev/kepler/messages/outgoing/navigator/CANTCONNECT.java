package org.alexdev.kepler.messages.outgoing.navigator;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CANTCONNECT extends MessageComposer {
    public enum QueueError {
        RESET("queue_reset"),
        FULL("queue_full"),
        STAFF_ONLY("na"),
        EVENT_PARTICIPENTS_ONLY_COPY("e2"),
        EVENT_PARTICIPENTS_ONLY("e1"),
        CLUB_ONLY("c");

        private final String reasonType;

        QueueError(String reasonType) {
            this.reasonType = reasonType;
        }

        public String getReasonType() {
            return this.reasonType;
        }
    }

    public enum ConnectError {
        ROOM_FULL(1),
        ROOM_CLOSED(2),
        QUEUE_ERROR(3),
        BANNED(4);

        private final int errorId;

        ConnectError(int errorId) {
            this.errorId = errorId;
        }

        public int getErrorId() {
            return this.errorId;
        }
    }

    private final ConnectError connectError;
    private final QueueError queueError;

    public CANTCONNECT(ConnectError error) {
        this.connectError = error;
        this.queueError = null;
    }

    public CANTCONNECT(QueueError error) {
        this.connectError = ConnectError.QUEUE_ERROR;
        this.queueError = error;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.connectError.getErrorId());

        if (this.queueError != null) {
            response.writeString(this.queueError.getReasonType());
        }
    }

    @Override
    public short getHeader() {
        return 224; // "C`"
    }
}

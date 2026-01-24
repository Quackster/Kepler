package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class STARTFAILED extends MessageComposer {
    public enum FailedReason {
        MINIMUM_TEAMS_REQUIRED(8);

        private final int reasonId;

        FailedReason(int reasonId) {
            this.reasonId = reasonId;
        }

        public int getReasonId() {
            return reasonId;
        }
    }

    private final FailedReason reason;
    private final String key;

    public STARTFAILED(FailedReason reason, String key) {
        this.reason = reason;
        this.key = key;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.reason.getReasonId());

        if (this.key != null) {
            response.writeString(this.key);
        }
    }

    @Override
    public short getHeader() {
        return 242;
    }
}

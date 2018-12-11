package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CREATEFAILED extends MessageComposer {
    public enum FailedReason {
        KICKED(6),
        MINIMUM_TEAMS_REQUIRED(8);

        private final int reasonId;

        FailedReason(int reasonId) {
            this.reasonId = reasonId;
        }

        public int getReasonId() {
            return reasonId;
        }
    }

    private final FailedReason failedReason;

    public CREATEFAILED(FailedReason failedReason) {
        this.failedReason = failedReason;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.failedReason.getReasonId());
    }

    @Override
    public short getHeader() {
        return 236; // "Cl"
    }
}

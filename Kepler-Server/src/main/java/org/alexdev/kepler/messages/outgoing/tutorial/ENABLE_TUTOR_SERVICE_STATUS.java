package org.alexdev.kepler.messages.outgoing.tutorial;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ENABLE_TUTOR_SERVICE_STATUS extends MessageComposer {
    public enum TutorEnableStatus {
        FRIENDSLIST_FULL(2),
        SERVICE_DISABLED(3),
        MAX_NEWBIES(4);

        private final int stateId;

        TutorEnableStatus(int stateId) {
            this.stateId = stateId;
        }

        public int getStateId() {
            return stateId;
        }
    }

    private final TutorEnableStatus status;

    public ENABLE_TUTOR_SERVICE_STATUS(TutorEnableStatus status) {
        this.status = status;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.status.getStateId());
    }

    @Override
    public short getHeader() {
        return 426;
    }
}

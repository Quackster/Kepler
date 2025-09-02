package net.h4bbo.kepler.messages.outgoing.alert;

import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class HOTEL_LOGOUT extends MessageComposer {
    public enum LogoutReason {
        DISCONNECT(-1),
        LOGGED_OUT(1),
        LOGOUT_CONCURRENT(2),
        LOGOUT_TIMEOUT(3);

        private final int msgId;

        LogoutReason(int msgId) {
            this.msgId = msgId;
        }

        public int getMsgId() {
            return msgId;
        }
    }
    private LogoutReason reason;

    public HOTEL_LOGOUT(LogoutReason reason) {
        this.reason = reason;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(reason.getMsgId());
    }

    @Override
    public short getHeader() {
        return 287; // "D_"
    }
}
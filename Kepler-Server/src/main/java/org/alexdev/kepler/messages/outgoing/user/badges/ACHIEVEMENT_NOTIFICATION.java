package org.alexdev.kepler.messages.outgoing.user.badges;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ACHIEVEMENT_NOTIFICATION extends MessageComposer {
    private final int typeId;
    private final int level;
    private final String badgeCode;
    private final String badgeRemove;

    public ACHIEVEMENT_NOTIFICATION(String badgeCode, String badgeRemove, int level) {
        this.typeId = 0;
        this.level = level;
        this.badgeCode = badgeCode;
        this.badgeRemove = badgeRemove;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.typeId);
        response.writeInt(this.level);
        response.writeString(this.badgeCode);
        response.writeString(this.badgeRemove == null ? "" : this.badgeRemove);
    }

    @Override
    public short getHeader() {
        return 437; // "Fu"
    }
}
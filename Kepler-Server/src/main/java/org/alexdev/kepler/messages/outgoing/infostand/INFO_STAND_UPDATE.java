package org.alexdev.kepler.messages.outgoing.infostand;

import org.alexdev.kepler.game.infostand.InfoStandActive;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class INFO_STAND_UPDATE extends MessageComposer {
    private final int userId;
    private final InfoStandActive infoStand;

    public INFO_STAND_UPDATE(int userId, InfoStandActive infoStand) {
        this.userId = userId;
        this.infoStand = infoStand;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.userId);
        response.writeString(this.infoStand.getExpression());
        response.writeString(this.infoStand.getAction());
        response.writeInt(this.infoStand.getDirection());
        response.writeInt(this.infoStand.getFurni());
        response.writeInt(this.infoStand.getPlate());
    }

    @Override
    public short getHeader() {
        return 143;
    }
}

package org.alexdev.kepler.messages.outgoing.infostand;

import org.alexdev.kepler.game.infostand.InfoStand;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class AVAILABLE_INFO_PROPS extends MessageComposer {
    private final InfoStand infoStand;

    public AVAILABLE_INFO_PROPS(InfoStand infoStand) {
        this.infoStand = infoStand;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.infoStand.getPlates().size());
        for (int plateId : this.infoStand.getPlates()) {
            response.writeInt(plateId);
        }

        response.writeInt(this.infoStand.getFurnis().size());
        for (int furnisId : this.infoStand.getFurnis()) {
            response.writeInt(furnisId);
        }

        response.writeInt(this.infoStand.getExpressions().size());
        for (String expression : this.infoStand.getExpressions()) {
            response.writeString(expression);
        }

        response.writeInt(this.infoStand.getActions().size());
        for (String action : this.infoStand.getActions()) {
            response.writeString(action);
        }

        response.writeInt(this.infoStand.getDirections().size());
        for (int direction : this.infoStand.getDirections()) {
            response.writeInt(direction);
        }
    }

    @Override
    public short getHeader() {
        return 142;
    }
}

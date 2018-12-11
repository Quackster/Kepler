package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_OBJECT extends MessageComposer {
    private final PlayerDetails details;

    public USER_OBJECT(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.details.getId());
        response.writeString(this.details.getName());
        response.writeString(this.details.getFigure());
        response.writeString(this.details.getSex());
        response.writeString(this.details.getMotto());
        response.writeInt(this.details.getTickets());
        response.writeString(this.details.getPoolFigure());
        response.writeInt(this.details.getFilm());
        //response.writeInt(this.details.getDirectMail()); TODO: figure out what directmail is used for in handleUserObj in hh_entry
    }

    @Override
    public short getHeader() {
        return 5; // "@E"
    }
}

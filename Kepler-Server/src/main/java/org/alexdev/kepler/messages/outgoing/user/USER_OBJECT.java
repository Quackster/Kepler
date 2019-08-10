package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.PlayerMessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_OBJECT extends PlayerMessageComposer {
    private final PlayerDetails details;
    private final int version;

    public USER_OBJECT(int version, PlayerDetails details) {
        this.version = version;
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        if (getPlayer().getVersion() == 9) {
            response.writeValue("name", this.details.getName());
            response.writeValue("figure", this.details.getFigure());
            response.writeValue("sex", Character.toString(this.details.getSex()));
            response.writeValue("customData", this.details.getMotto());
            response.writeValue("birthday", "01.01.1970");
            response.writeValue("has_read_agreement", "true");
        } else {
            response.writeString(this.details.getId());
            response.writeString(this.details.getName());
            response.writeString(this.details.getFigure());
            response.writeString(this.details.getSex());
            response.writeString(this.details.getMotto());
            response.writeInt(this.details.getTickets());
            response.writeString(this.details.getPoolFigure());
            response.writeInt(this.details.getFilm());
        }
        //response.writeInt(this.details.getDirectMail()); TODO: figure out what directmail is used for in handleUserObj in hh_entry
    }

    @Override
    public short getHeader() {
        return 5; // "@E"
    }
}

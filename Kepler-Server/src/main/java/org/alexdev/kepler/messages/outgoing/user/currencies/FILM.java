package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FILM extends MessageComposer {
    private final int film;

    public FILM(PlayerDetails details) {
        this.film = details.getFilm();
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.film);
    }

    @Override
    public short getHeader() {
        return 4; // "@D"
    }
}

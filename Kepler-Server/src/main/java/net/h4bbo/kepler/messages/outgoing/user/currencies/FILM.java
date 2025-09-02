package net.h4bbo.kepler.messages.outgoing.user.currencies;

import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

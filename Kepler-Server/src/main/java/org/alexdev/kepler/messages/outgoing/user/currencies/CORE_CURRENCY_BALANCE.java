package org.alexdev.kepler.messages.outgoing.user.currencies;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CORE_CURRENCY_BALANCE extends MessageComposer {
    private final PlayerDetails details;

    public CORE_CURRENCY_BALANCE(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.details.getCredits());
        response.writeInt(0); // TODO: HabloonBalance
        response.writeInt(this.details.getStamps());
        response.writeInt(0); // TODO: FishTokens
        response.writeInt(0); // TODO: GardeningTokens
    }

    @Override
    public short getHeader() {
        return 11;
    }
}

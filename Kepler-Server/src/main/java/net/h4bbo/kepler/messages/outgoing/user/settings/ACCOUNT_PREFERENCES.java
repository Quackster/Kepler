package net.h4bbo.kepler.messages.outgoing.user.settings;

import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class ACCOUNT_PREFERENCES extends MessageComposer {
    private final PlayerDetails details;

    public ACCOUNT_PREFERENCES(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.details.getSoundSetting());
        response.writeBool(false); //!this.details.isTutorialFinished());
    }

    @Override
    public short getHeader() {
        return 308; // "Dt": [[#tutorial_handler, #handleAccountPreferences]]
    }
}
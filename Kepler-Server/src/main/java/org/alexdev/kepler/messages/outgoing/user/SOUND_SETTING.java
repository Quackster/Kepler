package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SOUND_SETTING extends MessageComposer {
    private final PlayerDetails details;

    public SOUND_SETTING(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.details.getSoundSetting());
        response.writeInt(0); // TODO: find out why this is needed
    }

    @Override
    public short getHeader() {
        return 308; // "Dt": [[#login_handler, #handleSoundSetting]]
    }
}
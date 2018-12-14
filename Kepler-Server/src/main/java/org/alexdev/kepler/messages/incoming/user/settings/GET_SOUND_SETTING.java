package org.alexdev.kepler.messages.incoming.user.settings;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.settings.SOUND_SETTING;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_SOUND_SETTING implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        player.send(new SOUND_SETTING(player.getDetails()));
    }
}
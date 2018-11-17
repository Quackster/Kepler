package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SET_SOUND_SETTING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        boolean enabled = reader.readBoolean();
        player.getDetails().setSoundSetting(enabled);

        PlayerDao.saveSoundSetting(player.getDetails());
    }
}

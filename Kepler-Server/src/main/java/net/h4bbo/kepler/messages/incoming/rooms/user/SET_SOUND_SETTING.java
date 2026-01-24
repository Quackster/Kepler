package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class SET_SOUND_SETTING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        boolean enabled = reader.readBoolean();
        player.getDetails().setSoundSetting(enabled);

        PlayerDao.saveSoundSetting(player.getDetails().getId(), enabled);
    }
}

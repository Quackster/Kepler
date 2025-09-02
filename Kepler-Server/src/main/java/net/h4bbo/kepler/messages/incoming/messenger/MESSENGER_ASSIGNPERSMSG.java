package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.messenger.CONSOLE_MOTTO;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.StringUtil;

public class MESSENGER_ASSIGNPERSMSG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String consoleMotto = StringUtil.filterInput(reader.readString(), true);

        player.getMessenger().setPersistentMessage(consoleMotto);
    }
}

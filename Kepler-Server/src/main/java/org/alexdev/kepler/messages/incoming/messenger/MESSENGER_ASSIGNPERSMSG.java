package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.messenger.CONSOLE_MOTTO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class MESSENGER_ASSIGNPERSMSG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String consoleMotto = StringUtil.filterInput(reader.readString(), true);

        player.getMessenger().setPersistentMessage(consoleMotto);
    }
}

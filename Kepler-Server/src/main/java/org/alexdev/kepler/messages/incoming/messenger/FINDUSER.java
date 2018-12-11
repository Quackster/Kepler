package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.messenger.MESSENGER_SEARCH;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class FINDUSER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String searchQuery = reader.readString();

        int userId = MessengerDao.search(searchQuery);

        player.send(new MESSENGER_SEARCH(PlayerManager.getInstance().getPlayerData(userId)));
    }
}
